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
package io.wcm.wcm.core.components.impl.models.v1;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.Link;
import io.wcm.wcm.core.components.impl.models.helpers.NavigationItemV1Impl;
import io.wcm.wcm.core.components.impl.models.v2.NavigationV2Impl;

/**
 * wcm.io-based enhancements for {@link Navigation}:
 * <ul>
 * <li>Detect site root via URL handler</li>
 * <li>Build navigation item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Navigation.class, ComponentExporter.class },
    resourceType = NavigationV1Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationV1Impl extends NavigationV2Impl {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/navigation/v1/navigation";

  @Override
  protected NavigationItem newNavigationItem(@NotNull Page page, @NotNull Link link, int level, boolean active, boolean current,
      @NotNull List<NavigationItem> children) {
    return new NavigationItemV1Impl(page, link, level, active, current, children,
        getId(), getParentComponent());
  }

}
