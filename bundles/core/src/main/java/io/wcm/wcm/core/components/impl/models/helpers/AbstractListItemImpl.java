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

import static io.wcm.wcm.core.components.impl.models.helpers.IdGenerator.ID_SEPARATOR;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract helper class for ListItem implementations.
 * Generates an ID for the item, using the ID of its parent as a prefix
 */
public abstract class AbstractListItemImpl extends AbstractComponentImpl {

  protected final String parentId;

  private static final String ITEM_ID_PREFIX = "item";

  private String id;

  /**
   * @param parentId Parent ID
   * @param resource Resource
   */
  protected AbstractListItemImpl(@Nullable String parentId, @NotNull Resource resource) {
    this.resource = resource;
    this.parentId = parentId;
  }

  @Override
  public @Nullable String getId() {
    if (this.resource == null) {
      return null;
    }
    if (id == null) {
      ValueMap properties = resource.getValueMap();
      id = properties.get(PN_ID, String.class);
    }
    if (StringUtils.isEmpty(id)) {
      String prefix = StringUtils.join(parentId, ID_SEPARATOR, ITEM_ID_PREFIX);
      id = IdGenerator.generateId(prefix, resource.getPath());
    }
    else {
      id = StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trim(id)), " ", ID_SEPARATOR);
    }
    return id;
  }

}
