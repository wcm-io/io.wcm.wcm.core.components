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
package io.wcm.wcm.core.components.models;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.wcm.core.components.models.mixin.LinkMixin;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * Defines the {@code ResponsiveImage} Sling Model used for the
 * {@code /apps/wcm-io/wcm/core/components/responsiveimage} component.
 */
@ConsumerType
public interface ResponsiveImage extends Component, ComponentExporter, MediaMixin, LinkMixin {

  /**
   * Returns the value for the {@code alt} attribute of the image.
   * @return the value for the image's {@code alt} attribute, if one was set, or {@code null}
   */
  @Nullable
  String getAlt();

  /**
   * Returns the value for the image's {@code title} attribute, if one was set.
   * @return the value for the image's {@code title} attribute, if one was set, or {@code null}
   */
  @Nullable
  String getTitle();

  /**
   * Returns the value for the image's uuid, if one was set.
   * @return the value for the image's uuid, if one was set, or {@code null}
   */
  @Nullable
  String getUuid();

  /**
   * Checks if the image should display its caption as a popup (through the <code>&lt;img&gt;</code> {@code title}
   * attribute).
   * @return {@code true} if the caption should be displayed as a popup, {@code false} otherwise
   */
  boolean displayPopupTitle();

  /**
   * Returns the file reference of the current image, if one exists.
   * @return the file reference of the current image, if one exists, {@code null} otherwise
   */
  @JsonIgnore
  @Nullable
  String getFileReference();

  /**
   * Returns a list of image map areas.
   * @return the image map areas
   */
  @Nullable
  List<ImageArea> getAreas();

}
