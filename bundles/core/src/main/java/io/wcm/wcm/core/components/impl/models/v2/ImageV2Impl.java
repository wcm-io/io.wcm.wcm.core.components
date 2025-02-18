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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;

import io.wcm.handler.link.Link;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.Rendition;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaV1Impl;
import io.wcm.wcm.core.components.impl.models.v3.ImageV3Impl;
import io.wcm.wcm.core.components.impl.servlets.ImageWidthProxyServlet;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * wcm.io-based enhancements for {@link Image}:
 * <ul>
 * <li>Build image using Media handler</li>
 * <li>Build link using Link handler</li>
 * </ul>
 *
 * <p>
 * This image component does not take full use of all wcm.io Media handler features, but rather emulates
 * the behavior of the original AEM core image component and it special behavior.
 * </p>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Image.class, ComponentExporter.class },
    resourceType = ImageV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageV2Impl extends ImageV3Impl implements LinkMixin {

  /**
   * Resource type
   */
  public static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/image/v2/image";

  @Override
  protected Media buildMedia(boolean altFromAsset) {
    return HandlerUnwrapper.get(mediaHandler, resource)
        // disable dynamic media support as it is not compatible with the "src-pattern" concept
        .dynamicMediaDisabled(true)
        .decorative(isDecorative)
        .forceAltValueFromAsset(altFromAsset)
        .build();
  }

  @Override
  protected List<ImageArea> buildAreas() {
    return ImageAreaV1Impl.convertMap(media.getMap());
  }

  /**
   * Validates the configured list of widths, removes those that are bigger than the original rendition,
   * and returns them sorted by size.
   * @return Widths
   */
  @Override
  protected List<Long> buildRenditionWidths() {
    Rendition rendition = media.getRendition();
    if (rendition == null) {
      return List.of();
    }
    long maxWidth = rendition.getWidth();
    String[] configuredWidths = currentStyle.get(PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0]);
    return Arrays.stream(configuredWidths)
        .map(NumberUtils::toLong)
        .filter(width -> width > 0 && width <= maxWidth)
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * Build image url pattern based in ImageWidthServlet for dynamic rendition selection.
   * @return Url pattern
   */
  @Override
  protected String buildSrcPattern() {
    String mediaUrl = media.getUrl();
    String extension = StringUtils.substringAfterLast(mediaUrl, ".");
    String fileName = StringUtils.substringAfterLast(mediaUrl, "/");

    // build suffix containing file name and crop/rotation parameters as "cache killers"
    StringBuilder suffix = new StringBuilder();
    if (StringUtils.isNotEmpty(imageCrop)) {
      suffix.append(imageCrop).append("/");
    }
    if (StringUtils.isNotEmpty(imageRotate)) {
      suffix.append(imageRotate).append("/");
    }
    suffix.append(fileName);

    String url = urlHandler.get(resource)
        .selectors(ImageWidthProxyServlet.SELECTOR)
        .extension(extension)
        .suffix(suffix.toString())
        .buildExternalResourceUrl();

    // insert {.width} placeholder for rendition selection
    return StringUtils.replace(url, "." + ImageWidthProxyServlet.SELECTOR + ".",
        "." + ImageWidthProxyServlet.SELECTOR + WIDTH_PLACEHOLDER + ".");
  }

  @Override
  @NotNull
  public Link getLinkObject() {
    return link.getLinkObject();
  }

  @Override
  public String getSrc() {
    long noScriptWidth = getNoScriptWidth();
    if (noScriptWidth > 0) {
      return StringUtils.replace(srcPattern, WIDTH_PLACEHOLDER, "." + noScriptWidth);
    }
    else {
      return media.getUrl();
    }
  }

  /**
   * Picks a "medium" widths from the set of available widths.
   * @return Picked width
   */
  private long getNoScriptWidth() {
    if (widths.isEmpty()) {
      return 0;
    }
    return widths.get((int)Math.round(widths.size() / 2d - 0.5d));
  }

}
