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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.Link;

/**
 * {@link NavigationItem} implementation.
 */
public class NavigationItemV2Impl extends PageListItemV2Impl implements NavigationItem {

  private final Page page;
  private final int level;
  private final boolean active;
  private final boolean current;
  private final List<NavigationItem> children;

  /**
   * @param page Page
   * @param link Link
   * @param level Level
   * @param active Active
   * @param current Current
   * @param children Children
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  public NavigationItemV2Impl(@NotNull Page page, @NotNull Link link,
      int level, boolean active, boolean current, @NotNull List<NavigationItem> children,
      @Nullable String parentId, @Nullable Component parentComponent) {
    super(page, link, parentId, parentComponent);
    this.page = page;
    this.active = active;
    this.current = current;
    this.level = level;
    this.children = children;
  }

  @Override
  @Deprecated
  @JsonIgnore
  public Page getPage() {
    return page;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public boolean isCurrent() {
    return current;
  }

  @Override
  public List<NavigationItem> getChildren() {
    return children;
  }

  @Override
  public int getLevel() {
    return level;
  }

}
