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

import java.util.Calendar;
import java.util.Date;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;

/**
 * Implements the data layer functionality.
 */
public class ComponentDataImpl implements ComponentData {

  private static final Logger log = LoggerFactory.getLogger(ComponentDataImpl.class);

  protected final AbstractComponentImpl component;
  protected final Resource resource;

  /**
   * @param component Component
   * @param resource Resource
   */
  public ComponentDataImpl(@NotNull AbstractComponentImpl component, @Nullable Resource resource) {
    this.component = component;
    this.resource = resource;
  }

  @Override
  public String getId() {
    return component.getId();
  }

  @Override
  public String getParentId() {
    return null;
  }

  @Override
  public String getType() {
    if (resource != null) {
      return resource.getResourceType();
    }
    return null;
  }

  @Override
  public String getTitle() {
    return component.getDataLayerTitle();
  }

  @Override
  public String getDescription() {
    return component.getDataLayerDescription();
  }

  @Override
  public Date getLastModifiedDate() {
    Calendar lastModified = null;
    if (resource != null) {
      ValueMap valueMap = resource.adaptTo(ValueMap.class);
      if (valueMap != null) {
        lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        if (lastModified == null) {
          lastModified = valueMap.get(JcrConstants.JCR_CREATED, Calendar.class);
        }
      }
    }
    if (lastModified != null) {
      return lastModified.getTime();
    }
    return null;
  }

  @Override
  public String getText() {
    return component.getDataLayerText();
  }

  @Override
  public String getLinkUrl() {
    Link link = component.getDataLayerLink();
    if (link != null && link.isValid()) {
      return link.getUrl();
    }
    return null;
  }

  @Override
  public String getJson() {
    try {
      return String.format("{\"%s\":%s}",
          getId(),
          new ObjectMapper().writeValueAsString(this));
    }
    catch (JsonProcessingException ex) {
      log.error("Unable to generate dataLayer JSON string for {}", resource, ex);
    }
    return null;
  }

}
