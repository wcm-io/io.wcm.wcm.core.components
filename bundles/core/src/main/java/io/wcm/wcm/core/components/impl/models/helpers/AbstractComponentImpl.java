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

import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.Component;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
public abstract class AbstractComponentImpl extends AbstractComponentExporterImpl implements Component {

  private String id;

  @Override
  public @Nullable String getId() {
    if (id == null) {
      ValueMap properties = resource.getValueMap();
      id = properties.get(PN_ID, String.class);
    }
    return id;
  }

}
