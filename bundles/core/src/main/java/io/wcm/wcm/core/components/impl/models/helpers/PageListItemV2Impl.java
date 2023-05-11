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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

import io.wcm.handler.link.Link;

/**
 * {@link ListItem} implementation for page links.
 */
public class PageListItemV2Impl extends PageListItemV4Impl implements ListItem {

  /**
   * @param page Page
   * @param link Link
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   * @param showDescription Show description (for teaser)
   * @param linkItems Link items (for teaser)
   */
  public PageListItemV2Impl(@NotNull Page page, @NotNull Link link,
      @Nullable String parentId, @Nullable Component parentComponent,
      boolean showDescription, boolean linkItems) {
    super(page, link, parentId, parentComponent, showDescription, linkItems, null);
  }

  /**
   * @param page Page
   * @param link Link
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  public PageListItemV2Impl(@NotNull Page page, @NotNull Link link,
      @Nullable String parentId, @Nullable Component parentComponent) {
    this(page, link, parentId, parentComponent, false, false);
  }

}
