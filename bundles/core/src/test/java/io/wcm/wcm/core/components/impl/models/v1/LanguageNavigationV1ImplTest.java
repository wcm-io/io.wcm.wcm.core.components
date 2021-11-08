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

import static com.adobe.cq.wcm.core.components.models.LanguageNavigation.PN_NAVIGATION_ROOT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LANGUAGE;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.assertNavigationItems_DataLayer;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v1.LanguageNavigationV1Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.LANGUAGE_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertNavigationItems;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.day.cq.wcm.api.Page;

import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class LanguageNavigationV1ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page root_en;
  private Page root_fr;

  @BeforeEach
  void setUp() throws Exception {
    loadComponentDefinition(context, RESOURCE_TYPE);

    root_en = context.pageManager().getPage(CONTENT_ROOT);
    root_fr = context.create().page(LANGUAGE_ROOT + "/fr", null,
        ImmutableValueMap.of(JCR_LANGUAGE, Locale.FRENCH.toLanguageTag()));
  }

  @Test
  void testDefault() {
    context.currentResource(context.create().resource(root_en, "languageNavigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_NAVIGATION_ROOT, LANGUAGE_ROOT));
    LanguageNavigation underTest = AdaptTo.notNull(context.request(), LanguageNavigation.class);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());

    assertNavigationItems(underTest.getItems(), root_en, root_fr);
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings("null")
  void testDefault_DataLayer() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(root_en, "languageNavigation",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_NAVIGATION_ROOT, LANGUAGE_ROOT));
    LanguageNavigation underTest = AdaptTo.notNull(context.request(), LanguageNavigation.class);

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());

    assertNavigationItems_DataLayer(underTest.getItems(), root_en, root_fr);
    NavigationItem item1 = underTest.getItems().get(0);
    assertEquals("en-US", ((PageData)item1.getData()).getLanguage());
    NavigationItem item2 = underTest.getItems().get(1);
    assertEquals("fr", ((PageData)item2.getData()).getLanguage());
  }

}
