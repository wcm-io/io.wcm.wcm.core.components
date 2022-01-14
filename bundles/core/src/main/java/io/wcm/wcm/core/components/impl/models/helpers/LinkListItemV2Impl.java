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
package io.wcm.wcm.core.components.impl.models.helpers;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.SyntheticLinkResource;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;

/**
 * {@link ListItem} implementation for any links.
 */
public class LinkListItemV2Impl extends AbstractListItemImpl implements ListItem {

  private final String title;
  protected final LinkWrapper link;
  private final String itemIdPrefix;

  /**
   * @param title Title
   * @param link Link
   * @param itemIdPrefix Item ID prefix
   * @param parentId Parent Id
   * @param parentComponent The component that contains this list item
   * @param contextResource Resource in context of which this link item is used
   */
  public LinkListItemV2Impl(@NotNull String title, @NotNull LinkWrapper link, @NotNull String itemIdPrefix,
      @Nullable String parentId, @Nullable Component parentComponent, @NotNull Resource contextResource) {
    super(getLinkRequestResource(link, contextResource), parentId, parentComponent);
    this.title = title;
    this.link = link;
    this.itemIdPrefix = itemIdPrefix;
  }

  private static @NotNull Resource getLinkRequestResource(@NotNull LinkWrapper link, @NotNull Resource contextResource) {
    Resource resource = link.getLinkObject().getLinkRequest().getResource();
    if (resource == null) {
      resource = new SyntheticLinkResource(contextResource.getResourceResolver(), contextResource.getPath());
    }
    return resource;
  }

  @Override
  public @Nullable Link getLink() {
    return link.orNull();
  }

  /**
   * @deprecated Deprecated in API
   */
  @Override
  @Deprecated
  @JsonIgnore
  public String getURL() {
    return link.getURL();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  protected String getItemIdPrefix() {
    return itemIdPrefix;
  }

  // --- data layer ---

  @Override
  protected @NotNull PageData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asPage()
        .withTitle(this::getTitle)
        .withLinkUrl(this::getURL)
        .build();
  }

}
