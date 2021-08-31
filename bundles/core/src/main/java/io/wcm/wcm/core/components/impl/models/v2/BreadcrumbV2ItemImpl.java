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
package io.wcm.wcm.core.components.impl.models.v2;

import static org.apache.sling.api.SlingConstants.PROPERTY_PATH;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.models.helpers.NavigationItemV1Impl;

/**
 * {@link NavigationItem} wrapper for breadcrumb.
 */
@JsonIgnoreProperties({
    "page", "children", "level", "description", "lastModified", PROPERTY_PATH
})
public class BreadcrumbV2ItemImpl extends NavigationItemV1Impl implements NavigationItem {

  /**
   * @param page Page
   * @param link Link
   * @param level Level
   * @param current Current
   * @param children Children
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  public BreadcrumbV2ItemImpl(@NotNull Page page, @NotNull Link link,
      int level, boolean current, @NotNull List<NavigationItem> children,
      @Nullable String parentId, @Nullable Component parentComponent) {
    super(page, link, level, current, current, children, parentId, parentComponent);
  }

}
