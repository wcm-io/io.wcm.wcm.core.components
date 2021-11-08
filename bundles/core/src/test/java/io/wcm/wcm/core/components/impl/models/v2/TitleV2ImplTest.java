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
package io.wcm.wcm.core.components.impl.models.v2;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_EXTERNAL_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TYPE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_WINDOW_TARGET;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v2.TitleV2Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertInvalidLink;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertValidLink;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class TitleV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
  }

  @Test
  @SuppressWarnings("deprecation")
  void testEmpty() {
    context.currentResource(context.create().resource(page, "title",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Title underTest = AdaptTo.notNull(context.request(), Title.class);

    assertEquals("page1", underTest.getText());
    assertNull(underTest.getType());
    assertNull(underTest.getLinkURL());
    assertFalse(underTest.isLinkDisabled());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());

    assertInvalidLink(underTest);
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings({ "null", "deprecation" })
  void testProperties() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(page, "title",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        JCR_TITLE, "My Title",
        "type", "h3",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://host",
        PN_LINK_WINDOW_TARGET, "_blank"));
    Title underTest = AdaptTo.notNull(context.request(), Title.class);

    assertEquals("My Title", underTest.getText());
    assertEquals("h3", underTest.getType());
    assertEquals("http://host", underTest.getLinkURL());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    assertValidLink(underTest, "http://host", "_blank");

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
    assertEquals("My Title", data.getTitle());
    assertEquals("http://host", data.getLinkUrl());
  }

}
