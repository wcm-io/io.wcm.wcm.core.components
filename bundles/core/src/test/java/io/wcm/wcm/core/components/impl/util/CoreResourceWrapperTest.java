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
package io.wcm.wcm.core.components.impl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class CoreResourceWrapperTest {

  private static final String PATH = "/content/test";

  private final AemContext context = AppAemContext.newAemContext();

  @Test
  void testWrappingWithSimpleResource() {
    Resource resource = context.create().resource(PATH,
        "a", 1,
        "b", 2,
        ResourceResolver.PROPERTY_RESOURCE_TYPE, "a/b/c");
    Resource wrappedResource = new CoreResourceWrapper(resource, "d/e/f");

    Map<String, Object> expectedProperties = new HashMap<>(resource.getValueMap());
    expectedProperties.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "d/e/f");
    testValueMap(expectedProperties.entrySet(), wrappedResource.adaptTo(ValueMap.class));
    testValueMap(expectedProperties.entrySet(), wrappedResource.getValueMap());
    assertEquals("d/e/f", wrappedResource.getResourceType());
  }

  @Test
  void testWrappingWithHiddenProperties() {
    Resource resource = context.create().resource(PATH,
        "a", 1,
        "b", 2,
        ResourceResolver.PROPERTY_RESOURCE_TYPE, "a/b/c");
    Resource wrappedResource = new CoreResourceWrapper(resource, "d/e/f", null, Set.of("b"));

    Map<String, Object> expectedProperties = new HashMap<>(resource.getValueMap());
    expectedProperties.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "d/e/f");
    expectedProperties.remove("b");

    testValueMap(expectedProperties.entrySet(), wrappedResource.adaptTo(ValueMap.class));
    testValueMap(expectedProperties.entrySet(), wrappedResource.getValueMap());
    assertFalse(wrappedResource.getValueMap().containsKey("b"));
    assertEquals("d/e/f", wrappedResource.getResourceType());
  }

  @Test
  void testWrappingWithOverridenProperties() {
    Resource resource = context.create().resource(PATH,
        "a", "old-value-1",
        "c", "old-value-3");

    Map<String, Object> overriddenProperties = ImmutableValueMap.of(
        "a", "1",
        "b", "2");
    Resource wrappedResource = new CoreResourceWrapper(resource, "a/b/c", overriddenProperties, null);

    // isResourceType()
    assertTrue(wrappedResource.isResourceType("a/b/c"));

    // getResourceType()
    assertEquals("a/b/c", wrappedResource.getResourceType());

    // getValueMap()
    ValueMap properties = wrappedResource.getValueMap();
    assertEquals("1", properties.get("a", String.class));
    assertEquals("2", properties.get("b", String.class));
    assertEquals("old-value-3", properties.get("c", String.class));
  }

  @Test
  @SuppressWarnings("null")
  void testNulls() {
    assertThrows(IllegalArgumentException.class, () -> new CoreResourceWrapper(null, null));
  }

  @Test
  @SuppressWarnings("null")
  void isResourceTypeDelegated() {
    Resource toBeWrapped = mock(Resource.class);
    ResourceResolver resourceResolver = mock(ResourceResolver.class);
    when(toBeWrapped.getValueMap()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
    when(toBeWrapped.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.isResourceType(any(CoreResourceWrapper.class), any(String.class))).thenReturn(true);
    Resource wrappedResource = new CoreResourceWrapper(toBeWrapped, "a/b/c");
    assertTrue(wrappedResource.isResourceType("a/b/c"));
    verify(resourceResolver).isResourceType(wrappedResource, "a/b/c");
  }

  private void testValueMap(Collection<Map.Entry<String, Object>> keyValuePairs, ValueMap valueMap) {
    for (Map.Entry<String, Object> entry : keyValuePairs) {
      assertEquals(entry.getValue(), valueMap.get(entry.getKey()));
    }
  }

}
