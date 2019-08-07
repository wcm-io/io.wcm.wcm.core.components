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
package io.wcm.wcm.core.components.servlets;

import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.wcm.core.components.servlets.ImageWidthProxyServlet.SELECTOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.image.Layer;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.commons.contenttype.FileExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ImageWidthProxyServletTest {

  private final AemContext context = AppAemContext.newAemContext();

  private ImageWidthProxyServlet underTest;

  private Page page;
  private Asset asset;

  @Mock(lenient = true)
  private MockRequestDispatcherFactory requestDispatcherFactory;
  @Mock
  private RequestDispatcher requestDispatcher;

  @BeforeEach
  void setUp() {
    page = context.create().page(CONTENT_ROOT);
    asset = context.create().asset(DAM_ROOT + "/sample.jpg", 160, 90, ContentType.JPEG);

    underTest = context.registerInjectActivateService(new ImageWidthProxyServlet());

    context.request().setRequestDispatcherFactory(requestDispatcherFactory);
    context.requestPathInfo().setExtension(FileExtension.JPEG);

    when(requestDispatcherFactory.getRequestDispatcher(anyString(), any())).thenReturn(requestDispatcher);
  }

  @Test
  void testWithoutWidth() throws Exception {
    context.requestPathInfo().setSelectorString(SELECTOR);
    underTest.doGet(context.request(), context.response());
    assertEquals(HttpServletResponse.SC_NOT_FOUND, context.response().getStatus());
  }

  @Test
  void testWithInvalidMediaRef() throws Exception {
    context.requestPathInfo().setSelectorString(SELECTOR + ".100");
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/asset",
        PN_MEDIA_REF_STANDARD, "/invalid"));

    underTest.doGet(context.request(), context.response());
    assertEquals(HttpServletResponse.SC_NOT_FOUND, context.response().getStatus());
  }

  @Test
  void testValidMedia_ForwardImageFileServlet() throws Exception {
    context.requestPathInfo().setSelectorString(SELECTOR + ".100");
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/asset",
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    underTest.doGet(context.request(), context.response());
    assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());

    verify(requestDispatcherFactory).getRequestDispatcher(
        eq(DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original.image_file.100.56.file/sample.jpg"), any());
    verify(requestDispatcher).forward(context.request(), context.response());
  }

  @Test
  void testValidMedia_StreamRendition() throws Exception {
    context.requestPathInfo().setSelectorString(SELECTOR + ".160");
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/asset",
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    underTest.doGet(context.request(), context.response());
    assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());

    // validate streamed image
    assertEquals(ContentType.JPEG, context.response().getContentType());
    byte[] data = context.response().getOutput();
    try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
      Layer layer = new Layer(is);
      assertEquals(160, layer.getWidth());
    }
  }

}
