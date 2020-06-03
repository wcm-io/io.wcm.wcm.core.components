/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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
package io.wcm.wcm.core.components.impl.models.v1.datalayer;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagConstants;

/**
 * Data layer asset.
 */
public class AssetDataImpl implements AssetData {

  private final Asset asset;

  /**
   * @param asset Asset
   */
  public AssetDataImpl(@NotNull Asset asset) {
    this.asset = asset;
  }

  @Override
  public String getId() {
    return asset.getID();
  }

  @Override
  public Date getLastModifiedDate() {
    long assetLastModification = asset.getLastModified();
    Calendar created = null;
    if (assetLastModification == 0) {
      ValueMap resourceMap = asset.adaptTo(ValueMap.class);
      if (resourceMap != null) {
        created = resourceMap.get(JcrConstants.JCR_CREATED, Calendar.class);
        if (created != null) {
          assetLastModification = created.getTimeInMillis();
        }
      }
    }
    if (assetLastModification != 0) {
      return new Date(assetLastModification);
    }
    else {
      return null;
    }
  }

  @Override
  public String getFormat() {
    return asset.getMimeType();
  }

  @Override
  public String getUrl() {
    return asset.getPath();
  }

  @Override
  public String[] getTags() {
    List<String> assetTags = new LinkedList<>();
    String tagsValue = asset.getMetadataValueFromJcr(TagConstants.PN_TAGS);
    if (StringUtils.isNotEmpty(tagsValue)) {
      String[] tags = tagsValue.split(",");
      for (String tag : tags) {
        if (StringUtils.isNotEmpty(tag)) {
          assetTags.add(tag);
        }
      }
    }
    if (!assetTags.isEmpty()) {
      return assetTags.toArray(new String[0]);
    }
    else {
      return null;
    }
  }

}
