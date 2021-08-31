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
package io.wcm.wcm.core.components.impl.models.wcmio.v1;

import static com.adobe.cq.wcm.core.components.models.Image.PN_DISPLAY_POPUP_TITLE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_IS_DECORATIVE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_TITLE_VALUE_FROM_DAM;
import static com.adobe.cq.wcm.core.components.models.Image.PN_UUID_DISABLED;
import static com.day.cq.commons.ImageResource.PN_ALT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

import java.util.List;
import java.util.Optional;

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
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.AssetDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.commons.dom.HtmlElement;
import io.wcm.handler.commons.dom.Image;
import io.wcm.handler.commons.dom.Picture;
import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Asset;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.MediaNameConstants;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaV1Impl;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;
import io.wcm.wcm.core.components.models.ResponsiveImage;

/**
 * Responsive Image - wcm.io Core Component.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { ResponsiveImage.class, ComponentExporter.class },
    resourceType = ResponsiveImageV1Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ResponsiveImageV1Impl extends AbstractComponentImpl implements ResponsiveImage {

  /**
   * Resource type
   */
  public static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/wcmio/responsiveimage/v1/responsiveimage";

  @AemObject
  private Style currentStyle;
  @Self
  private LinkHandler linkHandler;
  @Self
  private MediaHandler mediaHandler;

  @ValueMapValue(name = PN_ALT, injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String alt;
  @ValueMapValue(name = JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String title;

  private Link link;
  private Media media;
  private String uuid;
  private String fileReference;
  private boolean displayPopupTitle;
  private List<ImageArea> areas;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();

    // read basic properties
    displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, true));
    boolean isDecorative = properties.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));

    // resolve media from DAM asset
    // add custom properties as defined in "image" core component
    media = HandlerUnwrapper.get(mediaHandler, resource)
        .property("itemprop", "contentUrl")
        .property("data-cmp-hook-image", "image")
        .property(MediaNameConstants.PROP_CSS_CLASS, "cmp-wcmio-responsiveimage__image")
        .decorative(isDecorative)
        .build();

    if (media.isValid() && !media.getRendition().isImage()) {
      // no image asset selected (cannot be rendered) - set to invalid
      media = mediaHandler.invalid();
    }
    if (media.isValid()) {
      initPropertiesFromDamAsset(properties);
      areas = ImageAreaV1Impl.convertMap(media.getMap());

      // display popup title
      if (this.displayPopupTitle() && media.getElement() != null) {
        setImageTitle(media.getElement(), getTitle());
      }
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

  @SuppressWarnings("unchecked")
  private static void setImageTitle(HtmlElement<?> element, String title) {
    if (element == null) {
      return;
    }
    if (element instanceof Picture || element instanceof Image) {
      element.setTitle(title);
    }
    else {
      List<HtmlElement<?>> children = (List)element.getChildren();
      for (HtmlElement<?> child : children) {
        setImageTitle(child, title);
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
  public boolean displayPopupTitle() {
    return displayPopupTitle;
  }

  @Override
  public String getFileReference() {
    return fileReference;
  }

  @Override
  public List<ImageArea> getAreas() {
    return areas;
  }

  // --- data layer ---

  @Override
  @SuppressWarnings("null")
  protected @NotNull ImageData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asImageComponent()
        .withTitle(this::getTitle)
        .withLinkUrl(this::getLinkURL)
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
