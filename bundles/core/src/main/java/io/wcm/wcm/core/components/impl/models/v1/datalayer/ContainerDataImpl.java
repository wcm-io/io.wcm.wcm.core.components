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
package io.wcm.wcm.core.components.impl.models.v1.datalayer;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;

import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;

/**
 * Data layer container.
 */
public class ContainerDataImpl extends ComponentDataImpl implements ContainerData {

  /**
   * @param component Component
   * @param resource resource
   */
  public ContainerDataImpl(@NotNull AbstractComponentImpl component, @Nullable Resource resource) {
    super(component, resource);
  }

  @Override
  public String[] getShownItems() {
    return component.getDataLayerShownItems();
  }

}
