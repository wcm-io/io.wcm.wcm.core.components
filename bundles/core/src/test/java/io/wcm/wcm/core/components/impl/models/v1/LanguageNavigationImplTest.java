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
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.AppAemContext.LANGUAGE_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertNavigationItems;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.v1.LanguageNavigationImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class LanguageNavigationImplTest {

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

    assertNavigationItems(underTest.getItems(), root_en, root_fr);
  }

}