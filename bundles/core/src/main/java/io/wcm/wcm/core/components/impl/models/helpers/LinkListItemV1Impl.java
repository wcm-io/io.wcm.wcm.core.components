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
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;

/**
 * {@link ListItem} implementation for any links.
 */
public class LinkListItemV1Impl extends LinkListItemV2Impl implements LinkMixin {

  /**
   * @param title Title
   * @param link Link
   * @param itemIdPrefix Item ID prefix
   * @param parentId Parent Id
   * @param parentComponent The component that contains this list item
   * @param contextResource Resource in context of which this link item is used
   */
  public LinkListItemV1Impl(@NotNull String title, @NotNull LinkWrapper link, @NotNull String itemIdPrefix,
      @Nullable String parentId, @Nullable Component parentComponent, @NotNull Resource contextResource) {
    super(title, link, itemIdPrefix, parentId, parentComponent, contextResource);
  }

  @Override
  public @NotNull Link getLinkObject() {
    return link.getLinkObject();
  }

  // overwrite to add @JsonIgnore
  @Override
  @JsonIgnore
  public com.adobe.cq.wcm.core.components.commons.link.Link getLink() {
    return super.getLink();
  }

  // overwrite to add @JsonIgnore(false)
  @Override
  @Deprecated
  @JsonIgnore(false)
  public String getURL() {
    return super.getURL();
  }

}
