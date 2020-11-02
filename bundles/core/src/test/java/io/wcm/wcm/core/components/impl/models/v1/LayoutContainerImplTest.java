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

import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v1.LayoutContainerImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class LayoutContainerImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
  }

  @Test
  void testEmpty() {
    context.currentResource(context.create().resource(page, "container",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    LayoutContainer underTest = AdaptTo.notNull(context.request(), LayoutContainer.class);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertNotNull(underTest.getId());
    assertNull(underTest.getData());
  }

  @Test
  @SuppressWarnings("null")
  void testEmpty_DataLayer() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(page, "container",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    LayoutContainer underTest = AdaptTo.notNull(context.request(), LayoutContainer.class);

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
  }

}
