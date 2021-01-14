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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.sling.models.annotations.AemObject;

/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl extends AbstractComponentImpl implements Container {

  @AemObject
  private Style currentStyle;
  @Self
  private MediaHandler mediaHandler;

  private String backgroundStyle;

  @Override
  public @Nullable String getBackgroundStyle() {
    if (backgroundStyle == null) {
      backgroundStyle = buildBackgroundStyle();
    }
    return StringUtils.trimToNull(backgroundStyle);
  }

  /**
   * Build background style attribute string.
   * @return Style string or empty string.
   */
  private @NotNull String buildBackgroundStyle() {
    StringBuilder style = new StringBuilder();

    boolean backgroundColorEnabled = currentStyle.get(PN_BACKGROUND_COLOR_ENABLED, false);
    boolean backgroundImageEnabled = currentStyle.get(PN_BACKGROUND_IMAGE_ENABLED, false);

    if (backgroundImageEnabled) {
      style.append(buildBackgroundStyle_BackgroundImage());
    }
    if (backgroundColorEnabled) {
      style.append(buildBackgroundStyle_BackgroundColor());
    }

    return style.toString();
  }

  /**
   * Gets background image reference and validates/resolves it using media handler.
   * @return Style string or empty string.
   */
  private @NotNull String buildBackgroundStyle_BackgroundImage() {
    // use unwrapped resource for handler processing to ensure the original resource type of the component is used
    Resource unwrappedResource = ResourceUtil.unwrap(resource);
    Media media = mediaHandler.get(unwrappedResource)
        .refProperty(PN_BACKGROUND_IMAGE_REFERENCE)
        .build();
    if (media.isValid()) {
      return "background-image:url(" + media.getUrl() + ");"
          + "background-size:cover;"
          + "background-repeat:no-repeat;";
    }
    return "";
  }

  /**
   * Gets background color.
   * @return Style string or empty string.
   */
  private @NotNull String buildBackgroundStyle_BackgroundColor() {
    ValueMap properties = resource.getValueMap();
    String backgroundColor = properties.get(PN_BACKGROUND_COLOR, String.class);
    if (!StringUtils.isBlank(backgroundColor)) {
      return "background-color:" + backgroundColor + ";";
    }
    return "";
  }

  @Override
  @NotNull
  protected ContainerData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asContainer()
        .withShownItems(this::getDataLayerShownItems)
        .build();
  }

  @JsonIgnore
  protected abstract String[] getDataLayerShownItems();

}
