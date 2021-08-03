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
package io.wcm.wcm.core.components.impl.models.helpers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ImageArea;

import io.wcm.handler.media.imagemap.ImageMapArea;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;

/**
 * Implementation of {@link ImageArea}.
 */
public class ImageAreaV2Impl implements ImageArea {

  private final ImageMapArea<io.wcm.handler.link.Link> delegate;
  private final LinkWrapper link;

  /**
   * @param delegate Delegate
   */
  public ImageAreaV2Impl(ImageMapArea<io.wcm.handler.link.Link> delegate) {
    this.delegate = delegate;
    io.wcm.handler.link.Link delegateLink = delegate.getLink();
    if (delegateLink != null) {
      this.link = new LinkWrapper(delegateLink);
    }
    else {
      this.link = null;
    }
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
  public Link getLink() {
    if (link != null) {
      return link.orNull();
    }
    else {
      return null;
    }
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, "link");
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, "link");
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
  @SuppressWarnings("unchecked")
  public static @Nullable List<ImageArea> convertMap(@Nullable List<ImageMapArea> imageMap) {
    if (imageMap == null) {
      return null;
    }
    return (List)imageMap.stream()
        .map(ImageAreaV2Impl::new)
        .collect(Collectors.toList());
  }

}
