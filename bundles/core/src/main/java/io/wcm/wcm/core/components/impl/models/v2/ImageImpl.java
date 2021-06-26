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

import static com.day.cq.commons.ImageResource.PN_ALT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.AssetDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Asset;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.Rendition;
import io.wcm.handler.url.UrlHandler;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaImpl;
import io.wcm.wcm.core.components.impl.servlets.ImageWidthProxyServlet;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * wcm.io-based enhancements for {@link Image}:
 * <ul>
 * <li>Build image using Media handler</li>
 * <li>Build link using Link handler</li>
 * </ul>
 * <p>
 * This image component does not take full use of all wcm.io Media handler features, but rather emulates
 * the behavior of the original AEM core image component and it special behavior.
 * </p>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Image.class, ComponentExporter.class },
    resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends AbstractComponentImpl implements Image, MediaMixin, LinkMixin {

  /**
   * Resource type
   */
  public static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/image/v2/image";
  private static final String WIDTH_PLACEHOLDER = "{.width}";

  @AemObject
  private Style currentStyle;
  @Self
  private LinkHandler linkHandler;
  @Self
  private MediaHandler mediaHandler;
  @Self
  private UrlHandler urlHandler;

  @ValueMapValue(name = PN_ALT, injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String alt;
  @ValueMapValue(name = JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String title;

  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String imageCrop;
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String imageRotate;

  private Link link;
  private Media media;
  private String uuid;
  private String fileReference;
  private boolean displayPopupTitle;
  private boolean enableLazyLoading;
  private int lazyThreshold;
  private boolean isDecorative;
  private List<ImageArea> areas;

  private List<Long> widths = Collections.emptyList();
  private long noScriptWidth;
  private String srcPattern;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();

    // read basic properties
    displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, true));
    enableLazyLoading = !currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, true);
    lazyThreshold = currentStyle.get(PN_DESIGN_LAZY_THRESHOLD, 0);
    isDecorative = properties.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));
    boolean altFromAsset = properties.get(PN_ALT_VALUE_FROM_DAM, currentStyle.get(PN_ALT_VALUE_FROM_DAM, true));

    // resolve media and properties from DAM asset
    media = HandlerUnwrapper.get(mediaHandler, resource)
        // disable dynamic media support as it is not compatible with the "src-pattern" concept
        .dynamicMediaDisabled(true)
        .decorative(isDecorative)
        .forceAltValueFromAsset(altFromAsset)
        .build();
    if (media.isValid() && !media.getRendition().isImage()) {
      // no image asset selected (cannot be rendered) - set to invalid
      media = mediaHandler.invalid();
    }
    if (media.isValid()) {
      initPropertiesFromDamAsset(properties);
      widths = buildRenditionWidths(media.getRendition());
      noScriptWidth = getNoScriptWidth();
      srcPattern = buildSrcPattern(media.getUrl());
      areas = ImageAreaImpl.convertMap(media.getMap());
    }

    // resolve link - decorative images have no link and no alt text by definition
    if (isDecorative) {
      link = linkHandler.invalid();
    }
    else {
      link = HandlerUnwrapper.get(linkHandler, resource).build();
    }
  }

  /**
   * Checks if the resolved media is a DAM asset, and initializes properties from it.
   * @param properties Resource properties
   */
  private void initPropertiesFromDamAsset(ValueMap properties) {
    Asset asset = media.getAsset();
    if (asset != null) {
      com.day.cq.dam.api.Asset damAsset = asset.adaptTo(com.day.cq.dam.api.Asset.class);
      if (damAsset != null) {
        boolean titleFromAsset = properties.get(PN_TITLE_VALUE_FROM_DAM, currentStyle.get(PN_TITLE_VALUE_FROM_DAM, true));
        boolean uuidDisabled = currentStyle.get(PN_UUID_DISABLED, false);

        fileReference = damAsset.getPath();
        alt = asset.getAltText();

        if (!uuidDisabled) {
          uuid = damAsset.getID();
        }

        if (titleFromAsset) {
          String assetTitle = asset.getTitle();
          if (StringUtils.isNotEmpty(assetTitle)) {
            title = assetTitle;
          }
        }
      }
    }
  }

  @Override
  @NotNull
  public Link getLinkObject() {
    return link;
  }

  @Override
  @NotNull
  public Media getMediaObject() {
    return media;
  }

  @Override
  public String getSrc() {
    if (noScriptWidth > 0) {
      return StringUtils.replace(srcPattern, WIDTH_PLACEHOLDER, "." + noScriptWidth);
    }
    else {
      return media.getUrl();
    }
  }

  @Override
  public String getAlt() {
    return alt;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getUuid() {
    return uuid;
  }

  @Override
  public String getLink() {
    return link.getUrl();
  }

  @Override
  public boolean displayPopupTitle() {
    return displayPopupTitle;
  }

  @Override
  public String getFileReference() {
    return fileReference;
  }

  @Override
  public boolean isLazyEnabled() {
    return enableLazyLoading;
  }

  @Override
  public int getLazyThreshold() {
    return lazyThreshold;
  }

  @Override
  public String getSrcUriTemplate() {
    return srcPattern;
  }

  @Override
  public int @NotNull [] getWidths() {
    return widths.stream()
        .mapToInt(Long::intValue)
        .toArray();
  }

  @Override
  public List<ImageArea> getAreas() {
    return areas;
  }

  @Override
  public boolean isDecorative() {
    return isDecorative;
  }

  @Override
  public String getJson() {
    // not required for image v2
    return null;
  }


  /**
   * Validates the configured list of widths, removes those that are bigger than the original rendition,
   * and returns them sorted by size.
   * @param rendition Primary rendition
   * @return Widths
   */
  private List<Long> buildRenditionWidths(Rendition rendition) {
    long maxWidth = rendition.getWidth();
    String[] configuredWidths = currentStyle.get(PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0]);
    return Arrays.stream(configuredWidths)
        .map(width -> NumberUtils.toLong(width))
        .filter(width -> width > 0 && width <= maxWidth)
        .sorted()
        .collect(Collectors.toList());
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

  /**
   * Build image url pattern based in ImageWidthServlet for dynamic rendition selection.
   * @param mediaUrl Media Url
   * @return Url pattern
   */
  private String buildSrcPattern(String mediaUrl) {
    String extension = StringUtils.substringAfterLast(media.getUrl(), ".");
    String fileName = StringUtils.substringAfterLast(media.getUrl(), "/");

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

  // --- data layer ---

  @Override
  @SuppressWarnings("null")
  protected @NotNull ImageData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asImageComponent()
        .withTitle(this::getTitle)
        .withLinkUrl(this::getLink)
        .withAssetData(() -> Optional.of(media)
            .filter(Media::isValid)
            .map(Media::getAsset)
            .map(asset -> AdaptTo.notNull(asset, com.day.cq.dam.api.Asset.class))
            .map(DataLayerBuilder::forAsset)
            .map(AssetDataBuilder::build)
            .orElse(null))
        .build();
  }

}
