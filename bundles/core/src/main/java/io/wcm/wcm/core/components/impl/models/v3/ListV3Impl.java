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
package io.wcm.wcm.core.components.impl.models.v3;

import java.util.Collection;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.models.helpers.PageListItemV2Impl;
import io.wcm.wcm.core.components.impl.models.v4.ListV4Impl;

/**
 * wcm.io-based enhancements for {@link List}:
 * <ul>
 * <li>Build link item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { List.class, ComponentExporter.class },
    resourceType = ListV3Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListV3Impl extends ListV4Impl {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/list/v3/list";

  @Override
  @JsonProperty("items")
  public @NotNull Collection<ListItem> getListItems() {
    return transformToPageListItems(getItems());
  }

  @Override
  protected ListItem newPageListItem(@NotNull Page page, @NotNull Link link, @Nullable String linkText) {
    return new PageListItemV2Impl(page, link,
        getId(), getParentComponent(), showDescription(), linkItems() || displayItemAsTeaser());
  }

}
