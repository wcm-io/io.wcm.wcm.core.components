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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ListItem;

import io.wcm.handler.link.Link;
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
   */
  public LinkListItemImpl(@NotNull String title, @NotNull Link link, @Nullable String parentId) {
    super(parentId, link.getLinkRequest().getResource());
    this.title = title;
    this.link = link;
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

  /*
   * DataLayerProvider implementation of field getters
   */

  @Override
  public String getDataLayerTitle() {
    return getTitle();
  }

  @Override
  public Link getDataLayerLink() {
    return link;
  }

}
