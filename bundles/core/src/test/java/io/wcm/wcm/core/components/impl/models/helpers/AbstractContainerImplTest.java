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

import static com.adobe.cq.wcm.core.components.models.Container.PN_BACKGROUND_COLOR;
import static com.adobe.cq.wcm.core.components.models.Container.PN_BACKGROUND_COLOR_ENABLED;
import static com.adobe.cq.wcm.core.components.models.Container.PN_BACKGROUND_IMAGE_ENABLED;
import static com.adobe.cq.wcm.core.components.models.Container.PN_BACKGROUND_IMAGE_REFERENCE;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.AppAemContext.DAM_ROOT;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;

@ExtendWith(AemContextExtension.class)
class AbstractContainerImplTest {

  private static final String RESOURCE_TYPE = "app1/components/comp1";
  private static final String BACKGROUND_COLOR = "#f00";
  private static final String VALID_BACKGROUND_IMAGE_REFERENCE = DAM_ROOT + "/test.jpg";
  private static final String INVALID_BACKGROUND_IMAGE_REFERENCE = DAM_ROOT + "/invalid.jpg";

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @BeforeEach
  void setUp() {
    context.addModelsForClasses(DummyContainer.class);
    page = context.pageManager().getPage(CONTENT_ROOT);
    context.create().asset(VALID_BACKGROUND_IMAGE_REFERENCE, 10, 10, ContentType.PNG);
  }

  // --- background style / background image ---

  @Test
  void testGetBackgroundStyle_Image_NotSet_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertNull(underTest.getBackgroundStyle());
  }

  @Test
  void testGetBackgroundStyle_Image_Set_Inactive() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_REFERENCE, VALID_BACKGROUND_IMAGE_REFERENCE));

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertNull(underTest.getBackgroundStyle());
  }

  @Test
  void testGetBackgroundStyle_Image_Set_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_REFERENCE, VALID_BACKGROUND_IMAGE_REFERENCE));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertEquals("background-image:url(/content/dam/sample/test.jpg/_jcr_content/renditions/original./test.jpg);"
        + "background-size:cover;"
        + "background-repeat:no-repeat;", underTest.getBackgroundStyle());
  }

  @Test
  void testGetBackgroundStyle_InvalidImage_Set_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_REFERENCE, INVALID_BACKGROUND_IMAGE_REFERENCE));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertNull(underTest.getBackgroundStyle());
  }

  // --- background style / background color ---

  @Test
  void testGetBackgroundStyle_Color_NotSet_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_COLOR_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertNull(underTest.getBackgroundStyle());
  }

  @Test
  void testGetBackgroundStyle_Color_Set_Inactive() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_COLOR, BACKGROUND_COLOR));

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertNull(underTest.getBackgroundStyle());
  }

  @Test
  void testGetBackgroundStyle_Color_Set_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_COLOR, BACKGROUND_COLOR));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_COLOR_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertEquals("background-color:#f00;", underTest.getBackgroundStyle());
  }

  // --- background style / combined ---

  @Test
  void testGetBackgroundStyle_Combined_Active() {
    context.currentResource(context.create().resource(page, "resource1",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_REFERENCE, VALID_BACKGROUND_IMAGE_REFERENCE,
        PN_BACKGROUND_COLOR, BACKGROUND_COLOR));
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_BACKGROUND_IMAGE_ENABLED, true,
        PN_BACKGROUND_COLOR_ENABLED, true);

    DummyContainer underTest = AdaptTo.notNull(context.request(), DummyContainer.class);

    assertEquals("background-image:url(/content/dam/sample/test.jpg/_jcr_content/renditions/original./test.jpg);"
        + "background-size:cover;"
        + "background-repeat:no-repeat;"
        + "background-color:#f00;", underTest.getBackgroundStyle());
  }

  @Model(adaptables = SlingHttpServletRequest.class)
  public static class DummyContainer extends AbstractContainerImpl {
    @Override
    protected String[] getDataLayerShownItems() {
      return null;
    }
  }

}
