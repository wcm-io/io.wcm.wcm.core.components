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
package io.wcm.wcm.core.components.impl.util;

import static com.adobe.cq.wcm.core.components.models.Image.PN_ALT_VALUE_FROM_DAM;
import static com.adobe.cq.wcm.core.components.models.Image.PN_ALT_VALUE_FROM_PAGE_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_IMAGE_FROM_PAGE_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_IS_DECORATIVE;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_ALTTEXT_STANDARD;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaBuilder;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.MediaInvalidReason;

/**
 * Resolves images and alt. texts for components either from the component resource,
 * or the feature image from a target page, depending on the components configuration.
 * This is currently used for Teaser and Responsive Image components.
 */
public class ComponentFeatureImageResolver {

  private final Resource componentResource;
  private final Page currentPage;
  private final MediaHandler mediaHandler;
  private final Map<String, Object> mediaHandlerProperties = new HashMap<>();

  private final Boolean imageFromPageImage;
  private final boolean altValueFromPageImage;
  private boolean altValueFromDam;
  private final boolean isDecorative;
  private final String componentAltText;

  private Page targetPage;

  /**
   * @param componentResource Component resource
   * @param currentPage Current page
   * @param mediaHandler Media handler
   */
  public ComponentFeatureImageResolver(@NotNull Resource componentResource,
      @NotNull Page currentPage, @NotNull Style currentStyle,
      @NotNull MediaHandler mediaHandler) {
    this.componentResource = componentResource;
    this.currentPage = currentPage;
    this.mediaHandler = mediaHandler;

    // component properties
    ValueMap props = componentResource.getValueMap();
    this.imageFromPageImage = props.get(PN_IMAGE_FROM_PAGE_IMAGE, Boolean.class);
    this.altValueFromPageImage = props.get(PN_ALT_VALUE_FROM_PAGE_IMAGE, false);
    this.altValueFromDam = props.get(PN_ALT_VALUE_FROM_DAM, false);
    this.isDecorative = props.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));
    this.componentAltText = props.get(PN_MEDIA_ALTTEXT_STANDARD, String.class);
  }

  /**
   * Set target page to get feature image from.
   * @param value Set target link page
   * @return self
   */
  public ComponentFeatureImageResolver targetPage(@Nullable Page value) {
    this.targetPage = value;
    return this;
  }

  /**
   * Add custom media handler property.
   * @param key Key
   * @param value Value
   * @return self
   */
  public ComponentFeatureImageResolver mediaHandlerProperty(@NotNull String key, @NotNull String value) {
    this.mediaHandlerProperties.put(key, value);
    return this;
  }

  /**
   * @param value Alt Value from DAM
   * @return self
   */
  public ComponentFeatureImageResolver altValueFromDam(boolean value) {
    this.altValueFromDam = value;
    return this;
  }

  /**
   * Build media after resolving feature images and alt. texts.
   * @return Media
   */
  public @NotNull Media buildMedia() {
    Media media = mediaHandler.invalid();

    boolean useImageFromPageImage = imageFromPageImage != null && imageFromPageImage;
    if (imageFromPageImage == null) {
      // image from resource properties
      media = buildMedia(componentResource);
      if (!media.isValid() && media.getMediaInvalidReason() == MediaInvalidReason.MEDIA_REFERENCE_MISSING) {
        // fallback to image from page if no reference was given and imageFromPageImage is neither enabled nor disabled
        useImageFromPageImage = true;
      }
    }

    if (useImageFromPageImage) {
      // try to get feature image from target page
      if (targetPage != null) {
        Resource featuredImageResource = ComponentUtils.getFeaturedImage(targetPage);
        media = buildMedia(wrapFeatureImageResource(featuredImageResource));
      }
      else {
        media = mediaHandler.invalid();
      }
      // if target page is not present or has not valid feature image, try to get it from current page
      if (!media.isValid() && isComponentInCurrentPage()) {
        Resource featuredImageResource = ComponentUtils.getFeaturedImage(currentPage);
        media = buildMedia(wrapFeatureImageResource(featuredImageResource));
      }
    }

    return media;
  }

  /**
   * Wraps the given resource and applies the original resource type from the component.
   * @param resource Resource
   * @return Wrapped resource
   */
  private @Nullable Resource wrapFeatureImageResource(@Nullable Resource resource) {
    if (resource == null) {
      return null;
    }
    return new CoreResourceWrapper(resource, HandlerUnwrapper.getResourceType(componentResource));
  }

  /**
   * Build media applying special CSS class derived from core components.
   * @param mediaResource Resource
   * @return Media
   */
  private @NotNull Media buildMedia(@Nullable Resource mediaResource) {
    if (mediaResource == null) {
      return mediaHandler.invalid();
    }
    MediaBuilder builder = HandlerUnwrapper.get(mediaHandler, mediaResource);

    if (isDecorative) {
      // skip all text. text if image is decorative
      builder.decorative(true);
    }
    else if (!(altValueFromPageImage || altValueFromDam)) {
      // explicitly apply alt. text from component if none of the "automatic features" is activated
      // otherwise rely to default media handler behavior
      builder.altText(componentAltText);
    }
    builder.forceAltValueFromAsset(altValueFromDam);

    // apply custom media handling properties
    mediaHandlerProperties.entrySet().forEach(entry -> builder.property(entry.getKey(), entry.getValue()));

    return builder.build();
  }

  /**
   * Fallback to current page is only done if the component resource is actually located in the current page
   * (or it's template).
   * @return true if component in current page
   */
  private boolean isComponentInCurrentPage() {
    String pageContentPath = currentPage.getContentResource().getPath();
    String templatePath = currentPage.getProperties().get(NameConstants.PN_TEMPLATE, String.class);
    return isChildResource(pageContentPath) || isChildResource(templatePath);
  }

  private boolean isChildResource(@Nullable String parentPath) {
    if (parentPath == null) {
      return false;
    }
    return StringUtils.startsWith(componentResource.getPath(), parentPath + "/");
  }

}
