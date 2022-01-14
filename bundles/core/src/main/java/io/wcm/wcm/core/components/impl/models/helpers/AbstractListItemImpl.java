/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.components.Component;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract helper class for ListItem implementations.
 * Generates an ID for the item, using the ID of its parent as a prefix
 */
public abstract class AbstractListItemImpl extends AbstractComponentImpl implements ListItem {

  protected final String parentId;
  protected final String dataLayerType;

  private static final String ITEM_ID_PREFIX = "item";

  /**
   * @param resource Resource
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  protected AbstractListItemImpl(@NotNull Resource resource,
      @Nullable String parentId, @Nullable Component parentComponent) {
    this.parentId = parentId;
    this.resource = resource;
    this.dataLayerType = parentComponent != null ? parentComponent.getResourceType() + "/" + ITEM_ID_PREFIX : null;
  }

  protected String getItemIdPrefix() {
    return ITEM_ID_PREFIX;
  }

  @Override
  @SuppressWarnings({ "null", "java:S2637" })
  @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
  public @NotNull String getId() {
    if (this.resource == null) {
      return null; // public Component interface allows null for id
    }
    return ComponentUtils.generateId(StringUtils.join(parentId, ComponentUtils.ID_SEPARATOR, getItemIdPrefix()), resource.getPath());
  }

  @Override
  @SuppressWarnings("null")
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData())
        .asComponent()
        .withType(() -> Optional.ofNullable(this.dataLayerType).orElseGet(() -> super.getComponentData().getType()))
        .withTitle(this::getTitle)
        .withLinkUrl(() -> Optional.ofNullable(this.getLink()).map(Link::getURL).orElse(null))
        .build();
  }

}
