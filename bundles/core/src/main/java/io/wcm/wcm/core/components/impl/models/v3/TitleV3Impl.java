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
package io.wcm.wcm.core.components.impl.models.v3;

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
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.commons.link.LinkWrapper;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;

/**
 * wcm.io-based enhancements for {@link Title}:
 * <ul>
 * <li>Build link using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Title.class, ComponentExporter.class },
    resourceType = TitleV3Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TitleV3Impl extends AbstractComponentImpl implements Title {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/title/v3/title";

  @Self
  @Via(type = ResourceSuperType.class)
  private Title delegate;

  @Self
  private LinkHandler linkHandler;
  protected LinkWrapper link;

  @PostConstruct
  private void activate() {
    link = new LinkWrapper(HandlerUnwrapper.get(linkHandler, resource).build());
  }

  @Override
  public @Nullable Link getLink() {
    return link.orNull();
  }

  // --- fallback implementations ---

  /**
   * @deprecated Deprecated in API
   */
  @Override
  @Deprecated
  @JsonIgnore
  public String getLinkURL() {
    return link.getURL();
  }

  // --- delegated methods ---

  @Override
  @SuppressWarnings("null")
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  public @NotNull String getId() {
    return this.delegate.getId();
  }

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

  // --- data layer ---

  @Override
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asComponent()
        .withTitle(this::getText)
        .withLinkUrl(link::getURL)
        .build();
  }

}
