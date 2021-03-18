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

import static com.adobe.cq.wcm.core.components.util.ComponentUtils.ID_SEPARATOR;

import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;

import io.wcm.sling.models.annotations.AemObject;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
public abstract class AbstractComponentImpl extends AbstractComponentExporterImpl implements Component {

  @AemObject
  protected Page currentPage;
  @AemObject
  protected ComponentContext componentContext;

  private String id;

  private Boolean dataLayerEnabled;
  private ComponentData componentData;

  @Override
  public @Nullable String getId() {
    if (id == null) {
      ValueMap properties = resource.getValueMap();
      id = properties.get(PN_ID, String.class);
    }
    if (StringUtils.isEmpty(id)) {
      id = ComponentUtils.getId(resource, currentPage, componentContext);
    }
    else {
      id = StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trim(id)), " ", ID_SEPARATOR);
    }
    return id;
  }

  private boolean isDataLayerEnabled() {
    if (dataLayerEnabled == null) {
      if (this.currentPage != null) {
        // Check at page level to allow components embedded via containers in editable templates to inherit the setting
        dataLayerEnabled = ComponentUtils.isDataLayerEnabled(this.currentPage.getContentResource());
      }
      else {
        dataLayerEnabled = ComponentUtils.isDataLayerEnabled(this.resource);
      }
    }
    return dataLayerEnabled;
  }


  /**
   * See {@link Component#getData()}
   * @return The component data
   */
  @Override
  @Nullable
  public ComponentData getData() {
    if (!isDataLayerEnabled()) {
      return null;
    }
    if (componentData == null) {
      componentData = getComponentData();
    }
    return componentData;
  }

  // --- Data layer specific methods ---
  // Each component can choose to implement some of these, to override or feed the data model.

  /**
   * Override this method to provide a different data model for your component. This will be called by
   * {@link AbstractComponentImpl#getData()} in case the datalayer is activated
   * @return The component data
   */
  @SuppressWarnings("null")
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.forComponent()
        .withId(this::getId)
        .withLastModifiedDate(() ->
        // Note: this can be simplified in JDK 11
        Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_LASTMODIFIED, Calendar.class))
            .map(Calendar::getTime)
            .orElseGet(() -> Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_CREATED, Calendar.class))
                .map(Calendar::getTime)
                .orElse(null)))
        .withType(() -> this.resource.getResourceType())
        .build();
  }

}
