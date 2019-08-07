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

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Title;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.models.LinkMixin;

/**
 * wcm.io-based enhancements for {@link Title}:
 * <ul>
 * <li>Build link using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Title.class, ComponentExporter.class },
    resourceType = TitleImpl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TitleImpl implements Title, LinkMixin {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/title/v2/title";

  @Self
  @Via(type = ResourceSuperType.class)
  private Title delegate;

  @SlingObject
  private Resource resource;
  @Self
  private LinkHandler linkHandler;
  private Link link;

  @PostConstruct
  private void activate() {
    link = linkHandler.get(resource).build();
  }

  @Override
  @NotNull
  public Link getLinkObject() {
    return link;
  }

  @Override
  public @NotNull String getExportedType() {
    return resource.getResourceType();
  }

  // --- fallback implementations ---

  @Override
  public String getLinkURL() {
    return link.getUrl();
  }

  // --- delegated methods ---

  @Override
  public String getText() {
    return this.delegate.getText();
  }

  @Override
  public String getType() {
    return this.delegate.getType();
  }

  @Override
  public boolean isLinkDisabled() {
    return this.delegate.isLinkDisabled();
  }

}
