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
package io.wcm.wcm.core.components.impl.models.helpers;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LANGUAGE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class LanguageNavigationItemV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private LinkHandler linkHandler;

  @BeforeEach
  void setUp() {
    linkHandler = AdaptTo.notNull(context.request(), LinkHandler.class);
  }

  @Test
  @SuppressWarnings("deprecation")
  void testValidLink() {
    Page page = context.create().page(CONTENT_ROOT + "/page1", null,
        ImmutableValueMap.of(JCR_DESCRIPTION, "My Description",
            JCR_LANGUAGE, "fr_CA"));
    Link link = linkHandler.get(page).build();
    LanguageNavigationItem underTest = new LanguageNavigationItemV2Impl(page, link,
        5, true, true, Collections.emptyList(), "My Title", "p-id", null);

    assertEquals(page.getPath(), underTest.getPage().getPath());
    assertTrue(underTest.isActive());
    assertTrue(underTest.isCurrent());
    assertEquals(Collections.emptyList(), underTest.getChildren());
    assertEquals(5, underTest.getLevel());

    assertEquals("My Title", underTest.getTitle());

    assertEquals(Locale.CANADA_FRENCH, underTest.getLocale());
    assertEquals("CA", underTest.getCountry());
    assertEquals("fr-CA", underTest.getLanguage());
  }

}
