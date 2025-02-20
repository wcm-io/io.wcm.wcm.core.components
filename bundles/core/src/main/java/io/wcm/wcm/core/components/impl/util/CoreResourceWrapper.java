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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ExporterConstants;

/**
 * Resource wrapper that overwrites the resource type of the target resource.
 * Additionally supports overwriting and hiding some properties.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public final class CoreResourceWrapper extends ResourceWrapper {

  private final String path;
  private final ValueMap valueMap;
  private final String overriddenResourceType;

  /**
   * @param resource Target resource
   * @param path Path of wrapped resource
   * @param overriddenResourceType New resource type
   */
  public CoreResourceWrapper(@NotNull Resource resource, @NotNull String path, @NotNull String overriddenResourceType) {
    this(resource, path, overriddenResourceType, null, null);
  }

  /**
   * @param resource Target resource
   * @param path Path of wrapped resource
   * @param overriddenResourceType New resource type
   * @param overriddenProperties Properties to add/overwrite in value map
   * @param hiddenProperties Properties to hide from value map
   */
  public CoreResourceWrapper(@NotNull Resource resource, @NotNull String path, @NotNull String overriddenResourceType,
      @Nullable Map<String, Object> overriddenProperties, @Nullable Set<String> hiddenProperties) {
    super(resource);
    this.path = path;
    if (StringUtils.isEmpty(overriddenResourceType)) {
      throw new IllegalArgumentException("The " + CoreResourceWrapper.class.getName() + " needs to override the resource type of " +
          "the wrapped resource, but the resourceType argument was null or empty.");
    }
    this.overriddenResourceType = overriddenResourceType;
    valueMap = new ValueMapDecorator(new HashMap<>(resource.getValueMap()));
    valueMap.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, overriddenResourceType);
    if (overriddenProperties != null) {
      valueMap.putAll(overriddenProperties);
    }
    if (hiddenProperties != null) {
      hiddenProperties.forEach(valueMap::remove);
    }
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
    if (type == ValueMap.class) {
      return (AdapterType)valueMap;
    }
    return super.adaptTo(type);
  }

  @Override
  @NotNull
  public ValueMap getValueMap() {
    return valueMap;
  }

  @Override
  @NotNull
  public String getResourceType() {
    return overriddenResourceType;
  }

  @Override
  public boolean isResourceType(String resourceType) {
    return this.getResourceResolver().isResourceType(this, resourceType);
  }

}
