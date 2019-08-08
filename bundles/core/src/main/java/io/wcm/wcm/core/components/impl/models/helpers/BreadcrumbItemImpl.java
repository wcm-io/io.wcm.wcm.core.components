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

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.wcm.handler.link.Link;

/**
 * {@link NavigationItem} wrapper for breadcrumb.
 */
@JsonIgnoreProperties(value = { "page", "children", "level", "description", "lastModified", "path" })
public class BreadcrumbItemImpl extends NavigationItemImpl implements NavigationItem {

  /**
   * @param page Page
   * @param link Link
   * @param active Active
   * @param level Level
   * @param children Children
   */
  public BreadcrumbItemImpl(@NotNull Page page, @NotNull Link link,
      boolean active, int level, @NotNull List<NavigationItem> children) {
    super(page, link, active, level, children);
  }

}