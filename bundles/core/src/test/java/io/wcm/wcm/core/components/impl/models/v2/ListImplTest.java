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

import static com.adobe.cq.wcm.core.components.models.List.PN_SOURCE;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertListItems;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.v2.ListImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ListImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page root;
  private Page page1;
  private Page page2;
  private Page page3;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    root = context.pageManager().getPage(CONTENT_ROOT);
    page1 = context.create().page(root, "page1");
    page2 = context.create().page(root, "page2");
    page3 = context.create().page(root, "page3");
  }

  @Test
  void testList() {
    context.currentResource(context.create().resource(root, "list",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_SOURCE, "children"));
    List underTest = AdaptTo.notNull(context.request(), List.class);

    Collection<ListItem> items = underTest.getListItems();
    assertListItems(items, page1, page2, page3);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

}
