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

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.components.Component;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.SyntheticLinkResource;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * {@link ListItem} implementation for any links.
 */
public class LinkListItemImpl extends AbstractListItemImpl implements ListItem, LinkMixin {

  private final String title;
  private final Link link;

  /**
   * @param title Title
   * @param link Link
   * @param parentId Parent Id
   * @param parentComponent The component that contains this list item
   * @param contextResource Resource in context of which this link item is used
   */
  public LinkListItemImpl(@NotNull String title, @NotNull Link link, @Nullable String parentId,
      @Nullable Component parentComponent, @NotNull Resource contextResource) {
    super(getLinkRequestResource(link, contextResource), parentId, parentComponent);
    this.title = title;
    this.link = link;
  }

  private static @NotNull Resource getLinkRequestResource(@NotNull Link link, @NotNull Resource contextResource) {
    Resource resource = link.getLinkRequest().getResource();
    if (resource == null) {
      resource = new SyntheticLinkResource(contextResource.getResourceResolver(), contextResource.getPath());
    }
    return resource;
  }

  @Override
  @NotNull
  public Link getLinkObject() {
    return link;
  }

  @Override
  public String getURL() {
    return link.getUrl();
  }

  @Override
  public String getTitle() {
    return title;
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
