/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
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
package io.wcm.wcm.core.components.impl.models.v1;

import static com.adobe.cq.wcm.core.components.models.Breadcrumb.PN_HIDE_CURRENT;
import static com.adobe.cq.wcm.core.components.models.Breadcrumb.PN_SHOW_HIDDEN;
import static com.day.cq.wcm.api.NameConstants.PN_HIDE_IN_NAV;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertNavigationItems;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.v1.BreadcrumbImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class BreadcrumbImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page root;
  private Page page1;
  private Page page2_hidden;
  private Page page3;

  @BeforeEach
  void setUp() throws Exception {
    loadComponentDefinition(context, RESOURCE_TYPE);

    root = context.pageManager().getPage(CONTENT_ROOT);
    page1 = context.create().page(root, "page1");
    page2_hidden = context.create().page(page1, "page2", null,
        ImmutableValueMap.of(PN_HIDE_IN_NAV, true));
    page3 = context.create().page(page2_hidden, "page3");
    context.currentPage(page3);
  }

  @Test
  void testDefault() {
    context.currentResource(context.create().resource(page3, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), root, page1, page3);
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

  @Test
  void testShowHidden() {
    context.currentResource(context.create().resource(page3, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SHOW_HIDDEN, true));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), root, page1, page2_hidden, page3);
  }

  @Test
  void testShowHidden_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_SHOW_HIDDEN, true);
    context.currentResource(context.create().resource(page3, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), root, page1, page2_hidden, page3);
  }

  @Test
  void testHideCurrent() {
    context.currentResource(context.create().resource(page3, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_HIDE_CURRENT, true));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), root, page1);
  }

  @Test
  void testHideCurrent_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_HIDE_CURRENT, true);
    context.currentResource(context.create().resource(page3, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), root, page1);
  }

  @Test
  void testOutsideRootPage() {
    Page testPage = context.create().page("/content/test");
    context.currentPage(testPage);

    context.currentResource(context.create().resource(testPage, "breadcrumb",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Breadcrumb underTest = AdaptTo.notNull(context.request(), Breadcrumb.class);

    assertNavigationItems(underTest.getItems(), testPage);
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

}
