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

import java.util.Calendar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * {@link ListItem} implementation for page links.
 */
public class PageListItemImpl extends AbstractListItemImpl implements ListItem, LinkMixin {

  private final Page page;
  private final Link link;

  /**
   * @param page Page
   * @param link Link
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  public PageListItemImpl(@NotNull Page page, @NotNull Link link,
      @Nullable String parentId, @Nullable Component parentComponent) {
    super(page.getContentResource(), parentId, parentComponent);
    this.page = page;
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
    String title = page.getNavigationTitle();
    if (title == null) {
      title = page.getPageTitle();
    }
    if (title == null) {
      title = page.getTitle();
    }
    if (title == null) {
      title = page.getName();
    }
    return title;
  }

  @Override
  public String getDescription() {
    return page.getDescription();
  }

  @Override
  public Calendar getLastModified() {
    return page.getLastModified();
  }

  @Override
  public String getPath() {
    return page.getPath();
  }

  @Override
  @JsonIgnore
  public String getName() {
    return page.getName();
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
