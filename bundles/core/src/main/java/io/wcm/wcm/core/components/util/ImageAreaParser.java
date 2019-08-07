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
package io.wcm.wcm.core.components.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ImageArea;

import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaImpl;

/**
 * Creates {@link ImageArea} from strings.
 */
public final class ImageAreaParser {

  private ImageAreaParser() {
    // static methods only
  }

  /**
   * Parses a map area string to {@link ImageArea} objects.
   * @param mapString Map string
   * @param linkHandler Link handler for resolving links defined in for map areas
   * @return List of areas or null
   */
  public static @Nullable List<ImageArea> buildFromMapString(@Nullable String mapString, @NotNull LinkHandler linkHandler) {
    if (StringUtils.isBlank(mapString)) {
      return null;
    }
    List<ImageArea> areas = new ArrayList<>();
    // Parse the image map areas as defined at {@code Image.PN_MAP}
    String[] mapAreas = StringUtils.split(mapString, "][");
    for (String area : mapAreas) {
      int coordinatesEndIndex = area.indexOf(")");
      if (coordinatesEndIndex < 0) {
        break;
      }
      String shapeAndCoords = StringUtils.substring(area, 0, coordinatesEndIndex + 1);
      String shape = StringUtils.substringBefore(shapeAndCoords, "(");
      String coordinates = StringUtils.substringBetween(shapeAndCoords, "(", ")");
      String remaining = StringUtils.substring(area, coordinatesEndIndex + 1);
      String[] remainingTokens = StringUtils.split(remaining, "|");
      if (StringUtils.isBlank(shape) || StringUtils.isBlank(coordinates)) {
        break;
      }
      if (remainingTokens.length > 0) {
        String href = StringUtils.removeAll(remainingTokens[0], "\"");

        // TODO: resolve/rewrite link via link handler

        if (StringUtils.isBlank(href)) {
          break;
        }

        String target = remainingTokens.length > 1 ? StringUtils.removeAll(remainingTokens[1], "\"") : "";
        String alt = remainingTokens.length > 2 ? StringUtils.removeAll(remainingTokens[2], "\"") : "";
        String relativeCoordinates = remainingTokens.length > 3 ? remainingTokens[3] : "";
        relativeCoordinates = StringUtils.substringBetween(relativeCoordinates, "(", ")");
        areas.add(new ImageAreaImpl(shape, coordinates, relativeCoordinates, href, target, alt));
      }
    }
    if (areas.isEmpty()) {
      return null;
    }
    else {
      return areas;
    }
  }

}
