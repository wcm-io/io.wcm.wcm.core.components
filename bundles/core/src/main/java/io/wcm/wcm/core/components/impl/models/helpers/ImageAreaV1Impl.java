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

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.media.imagemap.ImageMapArea;

/**
 * Implementation of {@link ImageArea}.
 */
public class ImageAreaV1Impl extends ImageAreaV2Impl {

  /**
   * @param delegate Delegate
   */
  public ImageAreaV1Impl(ImageMapArea<io.wcm.handler.link.Link> delegate) {
    super(delegate);
  }

  // overwrite to add @JsonIgnore
  @Override
  @JsonIgnore
  public Link getLink() {
    return super.getLink();
  }

  /**
   * Converts a image map of {@link ImageMapArea} objects to a {@link ImageArea} objects.
   * @param imageMap Image map
   * @return Converted image map
   */
  @SuppressWarnings("unchecked")
  public static @Nullable List<ImageArea> convertMap(@Nullable List<ImageMapArea> imageMap) {
    if (imageMap == null) {
      return null;
    }
    return (List)imageMap.stream()
        .map(ImageAreaV1Impl::new)
        .collect(Collectors.toList());
  }

}
