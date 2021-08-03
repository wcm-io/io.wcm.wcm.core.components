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
package io.wcm.wcm.core.components.impl.models.helpers;

import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertInvalidLink;
import static io.wcm.samples.core.testcontext.TestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.LinkHandler;
import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;

@ExtendWith(AemContextExtension.class)
class LinkListItemV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private LinkHandler linkHandler;

  @BeforeEach
  void setUp() {
    linkHandler = AdaptTo.notNull(context.request(), LinkHandler.class);
  }

  @Test
  void testValidLink() {
    Page page = context.create().page(CONTENT_ROOT + "/page1");
    LinkWrapper link = new LinkWrapper(linkHandler.get(page).build());
    ListItem underTest = new LinkListItemV2Impl("My Title", link, "id-prefix", "p-id", null, page.getContentResource());

    assertEquals("My Title", underTest.getTitle());

    assertValidLink(underTest.getLink(), page.getPath() + ".html");
  }

  @Test
  void testInvalidLink() {
    Page page = context.create().page(CONTENT_ROOT + "/page1");
    LinkWrapper link = new LinkWrapper(linkHandler.invalid());
    ListItem underTest = new LinkListItemV2Impl("My Title", link, "id-prefix", "p-id", null, page.getContentResource());

    assertInvalidLink(underTest.getLink());
  }

}
