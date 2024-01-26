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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * {@link ListItem} implementation for page links.
 */
@SuppressWarnings("java:S110") // class hierarchy levels
public class PageListItemV1Impl extends PageListItemV2Impl implements LinkMixin {

  /**
   * @param page Page
   * @param link Link
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  public PageListItemV1Impl(@NotNull Page page, @NotNull Link link,
      @Nullable String parentId, @Nullable Component parentComponent) {
    super(page, link, parentId, parentComponent, false, true);
  }

  @Override
  public @NotNull Link getLinkObject() {
    return link.getLinkObject();
  }

  // overwrite to add @JsonIgnore
  @Override
  @JsonIgnore
  public @Nullable com.adobe.cq.wcm.core.components.commons.link.Link getLink() {
    return super.getLink();
  }

  // overwrite to add @JsonIgnore(false)
  /**
   * @deprecated Deprecated in API
   */
  @Override
  @Deprecated(forRemoval = true)
  @JsonIgnore(false)
  public String getURL() {
    return super.getURL();
  }

}
