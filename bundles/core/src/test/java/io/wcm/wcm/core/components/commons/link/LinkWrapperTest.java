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
package io.wcm.wcm.core.components.commons.link;

import static io.wcm.handler.link.LinkNameConstants.PN_LINK_EXTERNAL_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TYPE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_WINDOW_TARGET;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import ch.randelshofer.io.ByteArrayImageInputStream;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class LinkWrapperTest {

  private final AemContext context = AppAemContext.newAemContext();

  private LinkHandler linkHandler;
  private Page page;

  @BeforeEach
  void setUp() {
    linkHandler = AdaptTo.notNull(context.request(), LinkHandler.class);
    page = context.create().page(CONTENT_ROOT + "/page1");
    context.currentPage(page);
  }

  @Test
  void testInvalid() {
    LinkWrapper underTest = new LinkWrapper(linkHandler.invalid());

    assertFalse(underTest.isValid());
    assertNull(underTest.getURL());
    assertNull(underTest.getMappedURL());
    assertNull(underTest.getExternalizedURL());
    assertEquals(Map.of(), underTest.getHtmlAttributes());
    assertNull(underTest.getReference());
    assertNotNull(underTest.getLinkObject());

    assertJson("{valid:false}", underTest);
  }

  @Test
  void testPageLink() {
    LinkWrapper underTest = new LinkWrapper(linkHandler.get(page).build());

    String url = page.getPath() + ".html";
    assertTrue(underTest.isValid());
    assertEquals(url, underTest.getURL());
    assertEquals(url, underTest.getMappedURL());
    assertEquals(url, underTest.getExternalizedURL());
    assertEquals(Map.of("href", url), underTest.getHtmlAttributes());
    assertEquals(page, underTest.getReference());
    assertNotNull(underTest.getLinkObject());

    assertJson("{valid:true,url:'" + url + "'}", underTest);
  }

  @Test
  @SuppressWarnings("null")
  void testAssetLink() {
    Asset asset = context.create().asset(DAM_ROOT + "/test.pdf", new ByteArrayImageInputStream(new byte[0]), ContentType.PDF);
    LinkWrapper underTest = new LinkWrapper(linkHandler.get(asset.getPath()).build());

    String url = "/content/dam/sample/test.pdf/_jcr_content/renditions/original./test.pdf";
    assertTrue(underTest.isValid());
    assertEquals(url, underTest.getURL());
    assertEquals(url, underTest.getMappedURL());
    assertEquals(url, underTest.getExternalizedURL());
    assertEquals(Map.of("href", url), underTest.getHtmlAttributes());
    assertTrue(underTest.getReference() instanceof io.wcm.handler.media.Asset);
    assertEquals(asset.getPath(), ((io.wcm.handler.media.Asset)underTest.getReference()).getPath());
    assertNotNull(underTest.getLinkObject());

    assertJson("{valid:true,url:'" + url + "'}", underTest);
  }

  @Test
  void testExternalLink() {
    String url = "https://wcm.io";
    String target = "_blank";
    Resource link = context.create().resource(page, "link",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, url,
        PN_LINK_WINDOW_TARGET, target);

    LinkWrapper underTest = new LinkWrapper(linkHandler.get(link).build());

    assertTrue(underTest.isValid());
    assertEquals(url, underTest.getURL());
    assertEquals(url, underTest.getMappedURL());
    assertEquals(url, underTest.getExternalizedURL());
    assertEquals(Map.of("href", url, "target", target), underTest.getHtmlAttributes());
    assertNull(underTest.getReference());
    assertNotNull(underTest.getLinkObject());

    assertJson("{valid:true,url:'" + url + "',attributes:{target:'" + target + "'}}", underTest);
  }

}
