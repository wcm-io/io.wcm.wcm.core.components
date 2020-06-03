/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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
package io.wcm.wcm.core.components.impl.models.v1.datalayer;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CREATED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_EXTERNAL_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TYPE;
import static io.wcm.wcm.core.components.impl.models.v1.datalayer.DataLayerTestUtils.formatDate;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.impl.models.helpers.IdGeneratorTest;

@ExtendWith(AemContextExtension.class)
class ComponentDataImplTest {

  private static final Calendar TIMESTAMP = Calendar.getInstance();

  private final AemContext context = AppAemContext.newAemContext();

  private DummyComponent component;

  @BeforeEach
  void setUp() {
    context.addModelsForClasses(DummyComponent.class);

    Page page = context.create().page(IdGeneratorTest.PAGE_PATH);
    Resource resource = context.create().resource(page, IdGeneratorTest.RESOURCE_NAME,
        PROPERTY_RESOURCE_TYPE, IdGeneratorTest.RESOURCE_TYPE,
        JCR_TITLE, "my-title",
        JCR_DESCRIPTION, "my-desc",
        JCR_CREATED, TIMESTAMP,
        "text", "my-text",
        PN_LINK_TYPE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost");
    context.currentResource(resource);

    component = AdaptTo.notNull(context.request(), DummyComponent.class);
  }

  @Test
  void testProperties() {
    ComponentData underTest = new ComponentDataImpl(component, context.currentResource());

    assertEquals(IdGeneratorTest.EXPECTED_ID, underTest.getId());
    assertNull(underTest.getParentId());
    assertEquals(IdGeneratorTest.RESOURCE_TYPE, underTest.getType());
    assertEquals("my-title", underTest.getTitle());
    assertEquals("my-desc", underTest.getDescription());
    assertEquals("my-text", underTest.getText());
    assertEquals(TIMESTAMP.getTime(), underTest.getLastModifiedDate());
    assertEquals("http://myhost", underTest.getLinkUrl());
  }

  @Test
  void testProperties_NullResource() {
    ComponentData underTest = new ComponentDataImpl(component, null);
    assertNull(underTest.getType());
    assertNull(underTest.getLastModifiedDate());
  }

  @Test
  void testJson() throws JSONException {
    ComponentData underTest = new ComponentDataImpl(component, context.currentResource());
    JSONAssert.assertEquals("{'" + IdGeneratorTest.EXPECTED_ID + "': {"
        + "'@type': '" + IdGeneratorTest.RESOURCE_TYPE + "', "
        + "'xdm:linkURL': 'http://myhost', "
        + "'dc:title': 'my-title', "
        + "'dc:description': 'my-desc', "
        + "'xdm:text': 'my-text', "
        + "'repo:modifyDate': '" + formatDate(TIMESTAMP) + "'"
        + "}}", underTest.getJson(), true);
  }

}
