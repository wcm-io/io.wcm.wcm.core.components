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
package io.wcm.wcm.core.components.impl.models.v2.form;

import static io.wcm.wcm.core.components.impl.models.v2.form.ContainerV2Impl.RESOURCE_TYPE;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.models.form.Container;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;

import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ContainerV2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @Mock
  private FormStructureHelperFactory formStructureHelperFactory;

  @BeforeEach
  void setUp() {
    context.registerService(FormStructureHelperFactory.class, formStructureHelperFactory);
    FormsHelperStubber.createStub();

    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
  }

  @Test
  void testEmpty() {
    context.currentResource(context.create().resource(page, "container",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    Container underTest = AdaptTo.notNull(context.request(), Container.class);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertEquals("/content/sample/en/page1.html", underTest.getAction());
    assertNull(underTest.getRedirect());
  }

  @Test
  void testWithRedirect() {
    Page thankYouPage = context.create().page(page, "thankyou");

    context.currentResource(context.create().resource(page, "container",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        "redirect", thankYouPage.getPath()));

    Container underTest = AdaptTo.notNull(context.request(), Container.class);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
    assertEquals("/content/sample/en/page1.html", underTest.getAction());
    assertEquals("/content/sample/en/page1/thankyou.html", underTest.getRedirect());
  }

}