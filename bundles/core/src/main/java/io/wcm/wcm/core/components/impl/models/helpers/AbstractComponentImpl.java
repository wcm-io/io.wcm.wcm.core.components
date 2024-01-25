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

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Component;
import com.day.cq.wcm.api.components.ComponentContext;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
@SuppressWarnings("java:S2176") // accept duplicate class name
public abstract class AbstractComponentImpl extends com.adobe.cq.wcm.core.components.util.AbstractComponentImpl implements ComponentExporter {

  @Override
  public @NotNull String getExportedType() {
    return resource.getResourceType();
  }

  protected @Nullable com.day.cq.wcm.api.components.Component getParentComponent() {
    ComponentContext compContext = this.componentContext;
    if (compContext != null) {
      return compContext.getComponent();
    }
    else {
      return null;
    }
  }

}
