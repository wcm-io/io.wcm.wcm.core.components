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
package io.wcm.wcm.core.components.impl.models.v2;

import static com.adobe.cq.wcm.core.components.models.Page.NN_PAGE_FEATURED_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Teaser.NN_ACTIONS;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_ACTIONS_DISABLED;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_ACTIONS_ENABLED;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_ACTION_TEXT;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_DESCRIPTION_FROM_PAGE;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_DESCRIPTION_HIDDEN;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_SHOW_TITLE_TYPE;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_TITLE_FROM_PAGE;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_TITLE_HIDDEN;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_TITLE_TYPE;
import static com.day.cq.commons.ImageResource.PN_ALT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
import static com.day.cq.dam.api.DamConstants.DC_TITLE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_CONTENT_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_EXTERNAL_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TYPE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_WINDOW_TARGET;
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_AUTOCROP;
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_FORMATS;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v2.TeaserV2Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertInvalidLink;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertInvalidMedia;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertValidLink;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertValidMedia;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.handler.link.type.InternalLinkType;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.models.ResponsiveImage;
import io.wcm.wcm.core.components.testcontext.AppAemContext;
import io.wcm.wcm.core.components.testcontext.MediaFormats;
import io.wcm.wcm.core.components.testcontext.ResourceTypeForcingResourceWrapper;

@ExtendWith(AemContextExtension.class)
class TeaserV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private Asset asset;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
    asset = context.create().asset(DAM_ROOT + "/sample.jpg", 160, 90, ContentType.JPEG,
        DC_TITLE, "Asset Title",
        DC_DESCRIPTION, "Asset Description");
    // create web rendition to test auto-cropping
    context.create().assetRenditionWebEnabled(asset);
  }

  @Test
  void testEmpty() {
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertFalse(underTest.isActionsEnabled());
    assertTrue(underTest.getActions().isEmpty());
    assertFalse(underTest.isImageLinkHidden());
    assertNull(underTest.getTitle());
    assertFalse(underTest.isTitleLinkHidden());
    assertNull(underTest.getDescription());
    assertNull(underTest.getTitleType());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());

    assertInvalidMedia(underTest);
    assertInvalidLink(underTest.getLink());
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings("null")
  void testWithImageAndPrimaryLink() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description",
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host",
        PN_LINK_WINDOW_TARGET, "_blank"));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertFalse(underTest.isActionsEnabled());
    assertTrue(underTest.getActions().isEmpty());
    assertFalse(underTest.isImageLinkHidden());
    assertEquals("Teaser Title", underTest.getTitle());
    assertFalse(underTest.isTitleLinkHidden());
    assertEquals("Teaser Description", underTest.getDescription());
    assertNull(underTest.getTitleType());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertValidMedia(underTest, "/content/dam/sample/sample.jpg/_jcr_content/renditions/original./sample.jpg");
    assertValidLink(underTest.getLink(), "http://host", "_blank");

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
    assertEquals("Teaser Title", data.getTitle());
    assertEquals("Teaser Description", data.getDescription());
    assertEquals("http://host", data.getLinkUrl());
  }

  @Test
  void testTitleDescriptionHidden() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_TITLE_HIDDEN, true,
        PN_DESCRIPTION_HIDDEN, true);
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description"));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertNull(underTest.getTitle());
    assertNull(underTest.getDescription());
  }

  @Test
  void testTitleDescriptionImageFromPage() {
    Page targetPage = context.create().page(page, "page1", null,
        JCR_TITLE, "Page Title",
        JCR_DESCRIPTION, "Page Description");
    context.create().resource(targetPage, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset.getPath());
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description",
        PN_LINK_TYPE, InternalLinkType.ID,
        PN_LINK_CONTENT_REF, targetPage.getPath(),
        PN_TITLE_FROM_PAGE, true,
        PN_DESCRIPTION_FROM_PAGE, true));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("Page Title", underTest.getTitle());
    assertEquals("Page Description", underTest.getDescription());
    assertValidMedia(underTest, "/content/dam/sample/sample.jpg/_jcr_content/renditions/original./sample.jpg");
  }

  @Test
  void testTitleDescriptionFromPage_HtmlReservedChars() {
    Page targetPage = context.create().page(page, "page1", null,
        JCR_TITLE, "Page Title",
        JCR_DESCRIPTION, "<&& cool page description &&>");
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description",
        PN_LINK_TYPE, InternalLinkType.ID,
        PN_LINK_CONTENT_REF, targetPage.getPath(),
        PN_TITLE_FROM_PAGE, true,
        PN_DESCRIPTION_FROM_PAGE, true));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("Page Title", underTest.getTitle());
    assertEquals("&lt;&amp;&amp; cool page description &amp;&amp;&gt;", underTest.getDescription());
  }

  @Test
  void testTitleDescriptionFromPage_NotEnabled() {
    Page targetPage = context.create().page(page, "page1", null,
        JCR_TITLE, "Page Title",
        JCR_DESCRIPTION, "Page Description");
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description",
        PN_LINK_TYPE, InternalLinkType.ID,
        PN_LINK_CONTENT_REF, targetPage.getPath()));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("Teaser Title", underTest.getTitle());
    assertEquals("Teaser Description", underTest.getDescription());
  }

  @Test
  void testTitleDescriptionFromPage_NoTargetPage() {
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "Teaser Title",
        JCR_DESCRIPTION, "Teaser Description",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host",
        PN_TITLE_FROM_PAGE, true,
        PN_DESCRIPTION_FROM_PAGE, true));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertNull(underTest.getTitle());
    assertNull(underTest.getDescription());
  }

  @Test
  void testWithActions() {
    Resource resource = context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host",
        PN_ACTIONS_ENABLED, true);
    context.currentResource(resource);
    context.create().resource(resource, NN_ACTIONS + "/action1",
        PN_ACTION_TEXT, "Action 1",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host/action1");
    context.create().resource(resource, NN_ACTIONS + "/action2_invalid");
    context.create().resource(resource, NN_ACTIONS + "/action3",
        PN_ACTION_TEXT, "Action 3",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host/action3");

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertTrue(underTest.isActionsEnabled());
    assertInvalidLink(underTest.getLink());

    assertEquals(2, underTest.getActions().size());

    ListItem action1 = underTest.getActions().get(0);
    assertEquals("Action 1", action1.getTitle());
    assertValidLink(action1.getLink(), "http://host/action1");

    ListItem action3 = underTest.getActions().get(1);
    assertEquals("Action 3", action3.getTitle());
    assertValidLink(action3.getLink(), "http://host/action3");
  }

  @Test
  void testWithActions_DisabledViaPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_ACTIONS_DISABLED, true);
    Resource resource = context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host",
        PN_ACTIONS_ENABLED, true);
    context.currentResource(resource);
    context.create().resource(resource, NN_ACTIONS + "/action1",
        PN_ACTION_TEXT, "Action 1",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host/action1");

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertFalse(underTest.isActionsEnabled());
    assertValidLink(underTest.getLink(), "http://host");

    assertTrue(underTest.getActions().isEmpty());
  }

  @Test
  void testTitleDescriptionImageFromFirstActionPage() {
    Page targetPage = context.create().page(page, "page1", null,
        JCR_TITLE, "Page Title",
        JCR_DESCRIPTION, "Page Description");
    context.create().resource(targetPage, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset.getPath());
    Resource resource = context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_ACTIONS_ENABLED, true,
        PN_TITLE_FROM_PAGE, true,
        PN_DESCRIPTION_FROM_PAGE, true);
    context.currentResource(resource);
    context.create().resource(resource, NN_ACTIONS + "/action1",
        PN_ACTION_TEXT, "Action 1",
        PN_LINK_TYPE, InternalLinkType.ID,
        PN_LINK_CONTENT_REF, targetPage.getPath());

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("Page Title", underTest.getTitle());
    assertEquals("Page Description", underTest.getDescription());
    assertValidMedia(underTest, "/content/dam/sample/sample.jpg/_jcr_content/renditions/original./sample.jpg");
  }

  @Test
  void testTitleType_PageOverrideNotAllowed() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_TITLE_TYPE, "h4");
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_TITLE_TYPE, "h5"));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("h4", underTest.getTitleType());
  }

  @Test
  void testTitleType_PageOverrideAllowed() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_TITLE_TYPE, "h4",
        PN_SHOW_TITLE_TYPE, true);
    context.currentResource(context.create().resource(page, "teaser",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_TITLE_TYPE, "h5"));

    Teaser underTest = AdaptTo.notNull(context.request(), Teaser.class);

    assertEquals("h5", underTest.getTitleType());
  }

  @Test
  void testWithAssetImage_AutoCropping_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_COMPONENT_MEDIA_FORMATS, new String[] { MediaFormats.SQUARE.getName() },
        PN_COMPONENT_MEDIA_AUTOCROP, true);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "Resource Title",
        PN_ALT, "Resource Alt"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    String expectedMediaUrl = DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original.image_file.90.90.35,0,125,90.file/sample.jpg";

    assertValidMedia(underTest, expectedMediaUrl);
    assertInvalidLink(underTest);
  }

  @Test
  void testWithAssetImage_AutoCropping_ContentPolicy_WrappedResource() {
    // prepare dummy component that delegates to the component under test
    final String DELEGATE_RESOURCE_TYPE = "myapp/components/delegate";
    context.create().resource("/apps/" + DELEGATE_RESOURCE_TYPE,
        "sling:resourceSuperType", RESOURCE_TYPE);

    context.contentPolicyMapping(DELEGATE_RESOURCE_TYPE,
        PN_COMPONENT_MEDIA_FORMATS, new String[] { MediaFormats.SQUARE.getName() },
        PN_COMPONENT_MEDIA_AUTOCROP, true);

    Resource resource = context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, DELEGATE_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "Resource Title",
        PN_ALT, "Resource Alt");

    // set context resource to wrapped resource
    context.currentResource(new ResourceTypeForcingResourceWrapper(resource, RESOURCE_TYPE));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    String expectedMediaUrl = DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original.image_file.90.90.35,0,125,90.file/sample.jpg";

    assertValidMedia(underTest, expectedMediaUrl);
    assertInvalidLink(underTest);
  }

}
