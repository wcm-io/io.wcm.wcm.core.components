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
package io.wcm.wcm.core.components.impl.models.v2;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Button;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;

/**
 * wcm.io-based enhancements for {@link Button}:
 * <ul>
 * <li>Build link using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Button.class, ComponentExporter.class },
    resourceType = ButtonV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ButtonV2Impl extends AbstractComponentImpl implements Button {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/button/v2/button";

  @Self
  @Via(type = ResourceSuperType.class)
  private Button delegate;

  @Self
  private LinkHandler linkHandler;
  protected LinkWrapper link;

  @PostConstruct
  private void activate() {
    link = new LinkWrapper(HandlerUnwrapper.get(linkHandler, resource).build());
  }

  @Override
  public Link getButtonLink() {
    return link.orNull();
  }

  // --- data layer ---

  @Override
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asComponent()
        .withTitle(this::getText)
        .withLinkUrl(link::getURL)
        .build();
  }

  // --- fallback implementations ---

  @Override
  @Deprecated
  @JsonIgnore
  public String getLink() {
    return link.getURL();
  }

  // --- delegated methods ---

  @Override
  public @Nullable String getId() {
    return this.delegate.getId();
  }

  @Override
  public String getText() {
    return this.delegate.getText();
  }

  @Override
  public String getIcon() {
    return this.delegate.getIcon();
  }

  @Override
  public String getAccessibilityLabel() {
    return this.delegate.getAccessibilityLabel();
  }

}
