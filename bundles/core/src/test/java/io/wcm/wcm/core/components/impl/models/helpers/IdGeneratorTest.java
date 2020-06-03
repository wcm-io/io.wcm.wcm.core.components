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
package io.wcm.wcm.core.components.impl.models.helpers;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class IdGeneratorTest {

  public static final String RESOURCE_TYPE = "app1/components/mycomp";
  public static final String PAGE_PATH = "/content/mypath";
  public static final String RESOURCE_NAME = "myresource";
  public static final String RESOURCE_PATH = PAGE_PATH + "/" + JCR_CONTENT + "/" + RESOURCE_NAME;
  public static final String EXPECTED_ID = "mycomp-153aa205b2";

  private final AemContext context = AppAemContext.newAemContext();

  @Test
  void testGenerateId() {
    assertEquals(EXPECTED_ID, IdGenerator.generateId("mycomp", RESOURCE_PATH));
  }

  @Test
  public void testGenerateIdForComponent_OnlyResource() {
    Resource resource = context.create().resource(RESOURCE_PATH,
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE);
    assertEquals(EXPECTED_ID, IdGenerator.generateIdForComponent(resource, null, null));
  }

  @Test
  public void testGenerateIdForComponent_ComponentContext() {
    Page page = context.create().page(PAGE_PATH);
    Resource resource = context.create().resource(page, RESOURCE_NAME,
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE);
    context.currentResource(resource);

    assertEquals(EXPECTED_ID, IdGenerator.generateIdForComponent(resource, context.currentPage(),
        WCMUtils.getComponentContext(context.request())));
  }

}
