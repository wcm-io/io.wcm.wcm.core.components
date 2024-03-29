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

import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v2.TextV2Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
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

import com.adobe.cq.wcm.core.components.models.Text;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class TextV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
  }

  @Test
  void testEmpty() {
    context.currentResource(context.create().resource(page, "text",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    Text underTest = AdaptTo.notNull(context.request(), Text.class);

    assertNull(underTest.getText());
    assertFalse(underTest.isRichText());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings("null")
  void testRichText() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(page, "text",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        "text", "<p>My Text</p>",
        "textIsRich", true));
    Text underTest = AdaptTo.notNull(context.request(), Text.class);

    assertEquals("<p>My Text</p>", underTest.getText());
    assertTrue(underTest.isRichText());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
    assertEquals("<p>My Text</p>", data.getText());
  }

  @Test
  void testPlainText() {
    context.currentResource(context.create().resource(page, "text",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        "text", "Line 1\nLine 2",
        "textIsRich", false));
    Text underTest = AdaptTo.notNull(context.request(), Text.class);

    assertEquals("Line 1<br />Line 2", underTest.getText());
    assertFalse(underTest.isRichText());
    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

}
