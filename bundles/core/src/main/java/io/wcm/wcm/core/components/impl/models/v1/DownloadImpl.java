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
package io.wcm.wcm.core.components.impl.models.v1;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
import static com.day.cq.dam.api.DamConstants.DC_TITLE;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Download;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.media.Asset;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaArgs;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.Rendition;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentExporterImpl;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * wcm.io-based enhancements for {@link Download}:
 * <ul>
 * <li>Get download asset link and metadata via Media handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Download.class, ComponentExporter.class },
    resourceType = DownloadImpl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DownloadImpl extends AbstractComponentExporterImpl implements Download, MediaMixin {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/download/v1/download";

  @AemObject
  private Style currentStyle;

  @ValueMapValue(name = JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
  private String title;
  @ValueMapValue(name = JCR_DESCRIPTION, injectionStrategy = InjectionStrategy.OPTIONAL)
  private String description;
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private String actionText;

  @Self
  private MediaHandler mediaHandler;

  private boolean titleFromAsset;
  private boolean descriptionFromAsset;
  private boolean inline;
  private boolean displaySize;
  private boolean displayFormat;
  private boolean displayFilename;

  private ValueMap properties;
  private String titleType;
  private String filename;
  private String format;
  private String size;
  private String extension;

  private Media media;

  @PostConstruct
  @SuppressWarnings("null")
  protected void initModel() {
    properties = resource.getValueMap();

    titleFromAsset = properties.get(PN_TITLE_FROM_ASSET, titleFromAsset);
    descriptionFromAsset = properties.get(PN_DESCRIPTION_FROM_ASSET, descriptionFromAsset);
    inline = properties.get(PN_INLINE, inline);
    if (currentStyle != null) {
      titleType = currentStyle.get(PN_TITLE_TYPE, String.class);
      displaySize = currentStyle.get(PN_DISPLAY_SIZE, true);
      displayFormat = currentStyle.get(PN_DISPLAY_FORMAT, true);
      displayFilename = currentStyle.get(PN_DISPLAY_FILENAME, true);
    }

    media = mediaHandler.get(resource, new MediaArgs()
        // only allow linking to "download" media formats
        .download(true)
        // set content disposition attachment when not inline
        .contentDispositionAttachment(!inline))
        .build();
    if (media.isValid()) {
      initPropertiesFromMedia();
    }
  }

  private void initPropertiesFromMedia() {
    Rendition rendition = media.getRendition();
    if (rendition != null) {
      filename = rendition.getFileName();
      format = rendition.getMimeType();
      size = FileUtils.byteCountToDisplaySize(rendition.getFileSize());
      extension = rendition.getFileExtension();
    }

    Asset asset = media.getAsset();
    if (asset != null) {
      com.day.cq.dam.api.Asset damAsset = asset.adaptTo(com.day.cq.dam.api.Asset.class);
      if (damAsset != null) {
        if (titleFromAsset) {
          String assetTitle = damAsset.getMetadataValueFromJcr(DC_TITLE);
          if (StringUtils.isNotEmpty(assetTitle)) {
            title = assetTitle;
          }
        }
        if (descriptionFromAsset) {
          String assetDescription = damAsset.getMetadataValueFromJcr(DC_DESCRIPTION);
          if (StringUtils.isNotEmpty(assetDescription)) {
            description = assetDescription;
          }
        }
      }
    }
  }

  @Override
  @NotNull
  public Media getMediaObject() {
    return media;
  }

  @Override
  public String getUrl() {
    return media.getUrl();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getActionText() {
    return actionText;
  }

  @Override
  public String getTitleType() {
    return titleType;
  }

  @Override
  public String getFilename() {
    return filename;
  }

  @Override
  public String getFormat() {
    return format;
  }

  @Override
  public String getSize() {
    return size;
  }

  @Override
  public boolean displaySize() {
    return displaySize;
  }

  @Override
  public boolean displayFormat() {
    return displayFormat;
  }

  @Override
  public boolean displayFilename() {
    return displayFilename;
  }

  @Override
  public String getExtension() {
    return extension;
  }

}
