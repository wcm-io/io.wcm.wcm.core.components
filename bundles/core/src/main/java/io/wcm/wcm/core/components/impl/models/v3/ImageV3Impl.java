/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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
package io.wcm.wcm.core.components.impl.models.v3;

import static com.day.cq.commons.ImageResource.PN_ALT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.handler.media.MediaNameConstants.PROP_CSS_CLASS;
import static io.wcm.handler.media.MediaNameConstants.URI_TEMPLATE_PLACEHOLDER_WIDTH;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Asset;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.Rendition;
import io.wcm.handler.media.UriTemplate;
import io.wcm.handler.media.UriTemplateType;
import io.wcm.handler.media.format.Ratio;
import io.wcm.handler.url.UrlHandler;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaV2Impl;
import io.wcm.wcm.core.components.impl.util.ComponentFeatureImageResolver;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * wcm.io-based enhancements for {@link Image}:
 * <ul>
 * <li>Build image using Media handler</li>
 * <li>Build link using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Image.class, ComponentExporter.class },
    resourceType = ImageV3Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageV3Impl extends AbstractComponentImpl implements Image, MediaMixin {

  /**
   * Resource type
   */
  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/image/v3/image";

  /**
   * Width placeholder for URI template
   */
  public static final String WIDTH_PLACEHOLDER = "{.width}";

  @AemObject
  protected Style currentStyle;
  @Self
  protected LinkHandler linkHandler;
  @Self
  protected MediaHandler mediaHandler;
  @Self
  protected UrlHandler urlHandler;

  @ValueMapValue(name = PN_ALT, injectionStrategy = InjectionStrategy.OPTIONAL)
  protected @Nullable String alt;
  @ValueMapValue(name = JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
  protected @Nullable String title;

  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  protected @Nullable String imageCrop;
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  protected @Nullable String imageRotate;

  protected LinkWrapper link;
  protected Media media;
  protected String uuid;
  protected String fileReference;
  protected boolean displayPopupTitle;
  protected boolean enableLazyLoading;
  protected int lazyThreshold;
  protected boolean isDecorative;
  protected List<ImageArea> areas;

  protected List<Long> widths = Collections.emptyList();
  protected String srcPattern;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();

    // read basic properties
    displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, true));
    enableLazyLoading = !currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, true);
    lazyThreshold = currentStyle.get(PN_DESIGN_LAZY_THRESHOLD, 0);
    isDecorative = properties.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));
    boolean altFromAsset = properties.get(PN_ALT_VALUE_FROM_DAM, currentStyle.get(PN_ALT_VALUE_FROM_DAM, true));
    boolean imageFromPageImage = properties.get(PN_IMAGE_FROM_PAGE_IMAGE, true);

    // resolve link - decorative images have no link and no alt text by definition
    if (isDecorative) {
      link = new LinkWrapper(linkHandler.invalid());
    }
    else {
      link = new LinkWrapper(HandlerUnwrapper.get(linkHandler, resource).build());
    }

    // resolve media and properties from DAM asset
    media = buildMedia(altFromAsset, imageFromPageImage);

    if (media.isValid() && !media.getRendition().isImage()) {
      // no image asset selected (cannot be rendered) - set to invalid
      media = mediaHandler.invalid();
    }
    if (media.isValid()) {
      initPropertiesFromDamAsset(properties);
      widths = buildRenditionWidths();
      srcPattern = buildSrcPattern();
      areas = buildAreas();
    }

  }

  protected Media buildMedia(boolean altFromAsset, boolean imageFromPageImage) {
    ComponentFeatureImageResolver imageResolver = new ComponentFeatureImageResolver(resource, getCurrentPage(), currentStyle, mediaHandler)
        .targetPage(getCurrentPage())
        .altValueFromDam(altFromAsset)
        .imageFromPageImage(imageFromPageImage)
        .mediaHandlerProperty(PROP_CSS_CLASS, "cmp-image__image")
        .mediaHandlerProperty("itemprop", "contentUrl");
    String imageTitle = title;
    if (displayPopupTitle && imageTitle != null) {
      imageResolver.mediaHandlerProperty("title", imageTitle);
    }
    return imageResolver.buildMedia();
  }

  protected List<ImageArea> buildAreas() {
    return ImageAreaV2Impl.convertMap(media.getMap());
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

  /**
   * Build lists of rendition widths based on the resolved media renditions
   * (those that share the same ratio as the primary rendition)
   * @return Widths
   */
  protected List<Long> buildRenditionWidths() {
    double primaryRatio = media.getRendition().getRatio();
    return media.getRenditions().stream()
        .filter(rendition -> Ratio.matches(rendition.getRatio(), primaryRatio))
        .map(Rendition::getWidth)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * Build image url pattern via Media Handler URI template.
   * @return Url pattern
   */
  protected String buildSrcPattern() {
    Rendition rendition = media.getRendition();
    if (!rendition.isImage() || rendition.isVectorImage()) {
      return null;
    }
    UriTemplate uriTempalte = rendition.getUriTemplate(UriTemplateType.SCALE_WIDTH);
    return StringUtils.replace(uriTempalte.getUriTemplate(), URI_TEMPLATE_PLACEHOLDER_WIDTH, WIDTH_PLACEHOLDER);
  }

  @Override
  @NotNull
  public Media getMediaObject() {
    return media;
  }

  @Override
  public String getSrc() {
    return media.getUrl();
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
  public com.adobe.cq.wcm.core.components.commons.link.Link getImageLink() {
    return link.orNull();
  }

  /**
   * @deprecated Deprecated in API
   */
  @Override
  @Deprecated(forRemoval = true)
  public String getLink() {
    return link.getURL();
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
  public String getWidth() {
    return null;
  }

  @Override
  public String getHeight() {
    return null;
  }

  @Override
  public @Nullable String getSizes() {
    return null;
  }

  @Override
  public String getSrcset() {
    return null;
  }

  @Override
  public String getSmartCropRendition() {
    return null;
  }

  @Override
  public boolean isDmImage() {
    return false;
  }


  // --- data layer ---

  @Override
  @JsonIgnore
  @SuppressWarnings("null")
  public @NotNull ImageData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asImageComponent()
        .withTitle(this::getTitle)
        .withLinkUrl(() -> this.link.getLinkObject().getUrl())
        .withAssetData(() -> Optional.of(media)
            .filter(Media::isValid)
            .map(Media::getAsset)
            .map(asset -> asset.adaptTo(com.day.cq.dam.api.Asset.class))
            .map(DataLayerBuilder::forAsset)
            .map(AssetDataBuilder::build)
            .orElse(null))
        .build();
  }

}
