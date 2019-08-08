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
package io.wcm.wcm.core.components.models.mixin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.wcm.handler.media.Media;

/**
 * Adds wcm.io Media support to model interface.
 */
@ConsumerType
@JsonIgnoreProperties(value = "mediaObject")
public interface MediaMixin {

  /**
   * Get wcm.io Media handler object
   * @return Media
   */
  @NotNull
  Media getMediaObject();

  /**
   * Returns true if the media was resolved successful.
   * @return Media is valid
   */
  default boolean isMediaValid() {
    return getMediaObject().isValid();
  }

  /**
   * Returns the XHTML markup for the resolved media object (if valid).
   * This is in most cases an img element, but may also contain other arbitrary markup.
   * @return Media markup
   */
  @Nullable
  default String getMediaMarkup() {
    return getMediaObject().getMarkup();
  }

}
