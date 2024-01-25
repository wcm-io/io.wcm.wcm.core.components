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
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_NAVIGATION_ROOT;
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_SKIP_NAVIGATION_ROOT;
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_STRUCTURE_DEPTH;
import static com.adobe.cq.wcm.core.components.models.Navigation.PN_STRUCTURE_START;
import static com.day.cq.wcm.api.NameConstants.PN_HIDE_IN_NAV;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.assertNavigationItems_DataLayer;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v1.NavigationV1Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.LANGUAGE_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertNavigationItems;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
@SuppressWarnings("java:S5976") // similar tests
class NavigationV1ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page languageRoot;
  private Page root;
  private Page page1;
  private Page page11;
  private Page page111;
  private Page page112;
  private Page page12;
  private Page page3;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    languageRoot = context.pageManager().getPage(LANGUAGE_ROOT);
    root = context.pageManager().getPage(CONTENT_ROOT);
    page1 = context.create().page(root, "page1");
    page11 = context.create().page(page1, "page11");
    page111 = context.create().page(page11, "page111");
    page112 = context.create().page(page11, "page112");
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
    assertNotNull(underTest.getId());
    assertNull(underTest.getData());

    assertNavigationItems(underTest.getItems(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page111, page112);
    assertNavigationItems(underTest.getItems().get(1).getChildren());
  }

  @Test
  @SuppressWarnings("null")
  void testDefault_DataLayer() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());

    assertNavigationItems_DataLayer(underTest.getItems(), page1, page3);
    assertNavigationItems_DataLayer(underTest.getItems().get(0).getChildren(), page11, page12);
    assertNavigationItems_DataLayer(underTest.getItems().get(0).getChildren().get(0).getChildren(), page111, page112);
    assertNavigationItems_DataLayer(underTest.getItems().get(1).getChildren());
  }

  @Test
  void testStructureStart_0() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 0,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertEquals("my-label", underTest.getAccessibilityLabel());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren().get(0).getChildren(), page111, page112);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

  @Test
  void testStructureStart_0_structureDepth_2() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 0,
        PN_STRUCTURE_DEPTH, 2,
        PN_COLLECT_ALL_PAGES, false,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertEquals("my-label", underTest.getAccessibilityLabel());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren().get(0).getChildren());
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

  @Test
  void testStructureStart_1() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertEquals("my-label", underTest.getAccessibilityLabel());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertNavigationItems(underTest.getItems(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page111, page112);
    assertNavigationItems(underTest.getItems().get(1).getChildren());
  }

  @Test
  void testStructureStart_2() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 2,
        "accessibilityLabel", "my-label"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertEquals("my-label", underTest.getAccessibilityLabel());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertNavigationItems(underTest.getItems(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page111, page112);
  }

  @Test
  void testNavigationRoot_RelativeRootPath() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        PN_NAVIGATION_ROOT, "page1"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page111, page112);
  }

  @Test
  void testNavigationRoot_RelativeRootPath_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        PN_NAVIGATION_ROOT, "page1");
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page111, page112);
  }

  @Test
  void testNavigationRoot_RelativeRootPath_Invalid() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        PN_NAVIGATION_ROOT, "page_not_existing"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems());
  }

  @Test
  void testNavigationRoot_AbsoluteRootPath() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        PN_NAVIGATION_ROOT, "/content/sample/en/page1"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page111, page112);
  }

  @Test
  void testNavigationRoot_AbsoluteRootPath_RewriteContext() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 1,
        PN_NAVIGATION_ROOT, "/content/other-sample/fr/page1"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page111, page112);
  }

  @Test
  void testNavigationRoot_AbsoluteRootPath_OutsideContext() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_STRUCTURE_START, 0,
        PN_NAVIGATION_ROOT, "/content/sample"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems(), languageRoot);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page1, page3);
  }

  @Test
  void testNavigationRoot_AbsoluteRootPath_Invalid() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_NAVIGATION_ROOT, "/content/sample/en/page_not_existing"));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertNavigationItems(underTest.getItems());
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
  @SuppressWarnings("deprecation")
  void testIncludeNavigationRoot_SkipNavigationRoot() {
    context.currentResource(context.create().resource(root, "navigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SKIP_NAVIGATION_ROOT, false));
    Navigation underTest = AdaptTo.notNull(context.request(), Navigation.class);

    assertTrue(underTest.getItems().get(0).isActive());

    assertNavigationItems(underTest.getItems(), root);
    assertNavigationItems(underTest.getItems().get(0).getChildren(), page1, page3);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren(), page11, page12);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(0).getChildren().get(0).getChildren(), page111, page112);
    assertNavigationItems(underTest.getItems().get(0).getChildren().get(1).getChildren());
  }

  @Test
  @SuppressWarnings("deprecation")
  void testIncludeNavigationRootAndStructureDepth_SkipNavigationRoot() {
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
  @SuppressWarnings("deprecation")
  void testIncludeNavigationRootAndStructureDepth_ContentPolicy_SkipNavigationRoot() {
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
