/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2021 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.wcm.core.components.impl.models.v3;

import static com.adobe.cq.wcm.core.components.models.List.PN_LINK_ITEMS;
import static com.adobe.cq.wcm.core.components.models.List.PN_SHOW_DESCRIPTION;
import static com.adobe.cq.wcm.core.components.models.List.PN_SOURCE;
import static com.adobe.cq.wcm.core.components.models.Page.NN_PAGE_FEATURED_IMAGE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_CONTENT_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TYPE;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.assertListItems_DataLayer;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v3.ListV3Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertListItems;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;

import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableList;

import io.wcm.handler.link.type.InternalLinkType;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class ListV3ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page root;
  private Page page1;
  private Page page2;
  private Page page3;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    root = context.pageManager().getPage(CONTENT_ROOT);
    page1 = context.create().page(root, "page1", null,
        JCR_TITLE, "Page 1",
        JCR_DESCRIPTION, "Description 1");
    page2 = context.create().page(root, "page2", null,
        JCR_TITLE, "Page 2",
        JCR_DESCRIPTION, "Description 2");
    page3 = context.create().page(root, "page3", null,
        JCR_TITLE, "Page 3",
        JCR_DESCRIPTION, "Description 3");
  }

  @Test
  void testList() {
    context.currentResource(context.create().resource(root, "list",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SOURCE, "children"));
    List underTest = AdaptTo.notNull(context.request(), List.class);

    Collection<ListItem> items = underTest.getListItems();
    assertListItems(items, page1, page2, page3);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings("null")
  void testList_DataLayer() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(root, "list",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SOURCE, "children"));
    List underTest = AdaptTo.notNull(context.request(), List.class);

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
    assertListItems_DataLayer(underTest.getListItems(), page1, page2, page3);
  }

  @Test
  void testTeaserResource_with_LinksDescriptionsImage() {
    Asset asset = context.create().asset(DAM_ROOT + "/sample.jpg", 160, 90, ContentType.JPEG);
    context.create().resource(page2, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset.getPath());

    context.currentResource(context.create().resource(root, "list",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SOURCE, "children",
        PN_SHOW_DESCRIPTION, true,
        PN_LINK_ITEMS, true));
    List underTest = AdaptTo.notNull(context.request(), List.class);

    java.util.List<ListItem> items = ImmutableList.copyOf(underTest.getListItems());
    assertListItems(items, page1, page2, page3);

    ValueMap props1 = ResourceUtil.getValueMap(items.get(0).getTeaserResource());
    assertEquals("Page 1", props1.get(JCR_TITLE, String.class));
    assertEquals("Description 1", props1.get(JCR_DESCRIPTION, String.class));
    assertEquals(InternalLinkType.ID, props1.get(PN_LINK_TYPE, String.class));
    assertEquals(page1.getPath(), props1.get(PN_LINK_CONTENT_REF, String.class));
    assertNull(props1.get(PN_MEDIA_REF_STANDARD, String.class));

    ValueMap props2 = ResourceUtil.getValueMap(items.get(1).getTeaserResource());
    assertEquals("Page 2", props2.get(JCR_TITLE, String.class));
    assertEquals("Description 2", props2.get(JCR_DESCRIPTION, String.class));
    assertEquals(InternalLinkType.ID, props1.get(PN_LINK_TYPE, String.class));
    assertEquals(page2.getPath(), props2.get(PN_LINK_CONTENT_REF, String.class));
    assertEquals(asset.getPath(), props2.get(PN_MEDIA_REF_STANDARD, String.class));

    ValueMap props3 = ResourceUtil.getValueMap(items.get(2).getTeaserResource());
    assertEquals("Page 3", props3.get(JCR_TITLE, String.class));
    assertEquals("Description 3", props3.get(JCR_DESCRIPTION, String.class));
    assertEquals(InternalLinkType.ID, props1.get(PN_LINK_TYPE, String.class));
    assertEquals(page3.getPath(), props3.get(PN_LINK_CONTENT_REF, String.class));
    assertNull(props3.get(PN_MEDIA_REF_STANDARD, String.class));
  }

  @Test
  void testTeaserResource_no_LinksDescriptionsImage() {
    context.currentResource(context.create().resource(root, "list",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SOURCE, "children"));
    List underTest = AdaptTo.notNull(context.request(), List.class);

    java.util.List<ListItem> items = ImmutableList.copyOf(underTest.getListItems());
    assertListItems(items, page1, page2, page3);

    ValueMap props1 = ResourceUtil.getValueMap(items.get(0).getTeaserResource());
    assertEquals("Page 1", props1.get(JCR_TITLE, String.class));
    assertNull(props1.get(JCR_DESCRIPTION, String.class));
    assertNull(props1.get(PN_LINK_TYPE, String.class));
    assertNull(props1.get(PN_LINK_CONTENT_REF, String.class));
    assertNull(props1.get(PN_MEDIA_REF_STANDARD, String.class));

    ValueMap props2 = ResourceUtil.getValueMap(items.get(1).getTeaserResource());
    assertEquals("Page 2", props2.get(JCR_TITLE, String.class));
    assertNull(props2.get(JCR_DESCRIPTION, String.class));
    assertNull(props2.get(PN_LINK_TYPE, String.class));
    assertNull(props2.get(PN_LINK_CONTENT_REF, String.class));
    assertNull(props2.get(PN_MEDIA_REF_STANDARD, String.class));

    ValueMap props3 = ResourceUtil.getValueMap(items.get(2).getTeaserResource());
    assertEquals("Page 3", props3.get(JCR_TITLE, String.class));
    assertNull(props3.get(JCR_DESCRIPTION, String.class));
    assertNull(props3.get(PN_LINK_TYPE, String.class));
    assertNull(props3.get(PN_LINK_CONTENT_REF, String.class));
    assertNull(props3.get(PN_MEDIA_REF_STANDARD, String.class));
  }

}
