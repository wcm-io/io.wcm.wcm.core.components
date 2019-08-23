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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ImageArea;

import io.wcm.handler.media.imagemap.ImageMapArea;

/**
 * Implementation of {@link ImageArea}.
 */
public class ImageAreaImpl implements ImageArea {

  private final ImageMapArea delegate;

  /**
   * @param delegate Delegate
   */
  public ImageAreaImpl(ImageMapArea delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getShape() {
    return delegate.getShape();
  }

  @Override
  public String getCoordinates() {
    return delegate.getCoordinates();
  }

  @Override
  public String getRelativeCoordinates() {
    return delegate.getRelativeCoordinates();
  }

  @Override
  public String getHref() {
    return delegate.getLinkUrl();
  }

  @Override
  public String getTarget() {
    return StringUtils.defaultString(delegate.getLinkWindowTarget());
  }

  @Override
  public String getAlt() {
    return StringUtils.defaultString(delegate.getAltText());
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  /**
   * Converts a image map of {@link ImageMapArea} objects to a {@link ImageArea} objects.
   * @param imageMap Image map
   * @return Converted image map
   */
  public static @Nullable List<ImageArea> convertMap(@Nullable List<ImageMapArea> imageMap) {
    if (imageMap == null) {
      return null;
    }
    return imageMap.stream()
        .map(ImageAreaImpl::new)
        .collect(Collectors.toList());
  }

}
