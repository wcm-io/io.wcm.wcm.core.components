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

import static com.adobe.cq.wcm.core.components.models.Navigation.PN_COLLECT_ALL_PAGES;
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_SKIP_NAVIGATION_ROOT;
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_STRUCTURE_DEPTH;
import static com.day.cq.wcm.api.NameConstants.PN_HIDE_IN_NAV;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertNavigationItems;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.v1.NavigationImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Navigation;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class NavigationImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page root;
  private Page page1;
  private Page page11;
  private Page page12;
  private Page page3;

  @BeforeEach
  void setUp() throws Exception {
    loadComponentDefinition(context, RESOURCE_TYPE);

    root = context.pageManager().getPage(CONTENT_ROOT);
    page1 = context.create().page(root, "page1");
    page11 = context.create().page(page1, "page11");
    page12 = context.create().page(page1, "page12");
    context.create().page(root, "page2", null, ImmutableValueMap.of(PN_HIDE_IN_NAV, true));
    page3 = context.create().page(root, "page3");
  }

  @Test
  void testDefault() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertEquals("my-label", underTest.getAccessibilityLabel());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertNavigationItems(underTest.getItems(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(1).getChildren());
  }

  @Test
  void testStructureDepth() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_DEPTH, 1,
        PN_COLLECT_ALL_PAGES, false));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren());
    assertNavigationItems(underTest.getItems().get(1).getChildren());
  }

  @Test
  void testIncludeNavigationRoot() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SKIP_NAVIGATION_ROOT, false));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertTrue(underTest.getItems().get(0).isActive());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

  @Test
  void testIncludeNavigationRootAndStructureDepth() {
    context.currentResource(context.create().resource(page3, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SKIP_NAVIGATION_ROOT, false,
        PN_STRUCTURE_DEPTH, 1,
        PN_COLLECT_ALL_PAGES, false));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertTrue(underTest.getItems().get(0).isActive());
    assertFalse(underTest.getItems().get(0).getChildren().get(0).isActive());
    assertTrue(underTest.getItems().get(0).getChildren().get(1).isActive());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren());
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

  @Test
  void testIncludeNavigationRootAndStructureDepth_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_SKIP_NAVIGATION_ROOT, false,
        PN_STRUCTURE_DEPTH, 1,
        PN_COLLECT_ALL_PAGES, false);
    context.currentResource(context.create().resource(page3, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertTrue(underTest.getItems().get(0).isActive());
    assertFalse(underTest.getItems().get(0).getChildren().get(0).isActive());
    assertTrue(underTest.getItems().get(0).getChildren().get(1).isActive());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren());
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

}
