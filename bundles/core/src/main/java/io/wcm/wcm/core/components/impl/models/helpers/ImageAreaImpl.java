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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.adobe.cq.wcm.core.components.models.ImageArea;

/**
 * Implementation of {@link ImageArea}.
 */
public class ImageAreaImpl implements ImageArea {

  private final String shape;
  private final String coordinates;
  private final String relativeCoordinates;
  private final String href;
  private final String target;
  private final String alt;

  /**
   * @param shape Shape
   * @param coordinates coordinates
   * @param relativeCoordinates Relative coordinates
   * @param href href
   * @param target Target
   * @param alt Alt. text
   */
  public ImageAreaImpl(String shape, String coordinates, String relativeCoordinates, String href, String target, String alt) {
    this.shape = shape;
    this.coordinates = coordinates;
    this.relativeCoordinates = relativeCoordinates;
    this.href = href;
    this.target = target;
    this.alt = alt;
  }

  @Override
  public String getShape() {
    return shape;
  }

  @Override
  public String getCoordinates() {
    return coordinates;
  }

  @Override
  public String getRelativeCoordinates() {
    return relativeCoordinates;
  }

  @Override
  public String getHref() {
    return href;
  }

  @Override
  public String getTarget() {
    return target;
  }

  @Override
  public String getAlt() {
    return alt;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

}
