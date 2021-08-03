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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Title;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.models.v3.TitleV3Impl;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * wcm.io-based enhancements for {@link Title}:
 * <ul>
 * <li>Build link using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Title.class, ComponentExporter.class },
    resourceType = TitleV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TitleV2Impl extends TitleV3Impl implements LinkMixin {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/title/v2/title";

  @Override
  public @NotNull Link getLinkObject() {
    return link.getLinkObject();
  }

  // overwrite to add @JsonIgnore
  @Override
  @JsonIgnore
  public com.adobe.cq.wcm.core.components.commons.link.Link getLink() {
    return super.getLink();
  }

  // overwrite to add @JsonIgnore(false)
  @Override
  @Deprecated
  @JsonIgnore(false)
  @JsonProperty("linkURL")
  public String getLinkURL() {
    return super.getLinkURL();
  }

}
