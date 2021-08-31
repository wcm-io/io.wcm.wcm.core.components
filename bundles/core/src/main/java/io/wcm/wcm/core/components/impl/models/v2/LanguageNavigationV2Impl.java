/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2021 wcm.io
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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.LanguageNavigationItemV2Impl;

/**
 * wcm.io-based enhancements for {@link LanguageNavigation}:
 * <ul>
 * <li>Build language navigation item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { LanguageNavigation.class, ComponentExporter.class },
    resourceType = LanguageNavigationV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class LanguageNavigationV2Impl extends AbstractComponentImpl implements LanguageNavigation {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/languagenavigation/v2/languagenavigation";

  @Self
  @Via(type = ResourceSuperType.class)
  private LanguageNavigation delegate;

  @Self
  private LinkHandler linkHandler;

  @Override
  public List<NavigationItem> getItems() {
    return toLanguageNavigationItems(this.delegate.getItems());
  }

  private List<NavigationItem> toLanguageNavigationItems(List<NavigationItem> items) {
    return items.stream()
        .map(this::toLanguageNavigationItem)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("deprecation")
  private NavigationItem toLanguageNavigationItem(NavigationItem item) {
    Page page = item.getPage();
    Link link = linkHandler.get(page).build();
    return newLanguageNavigationItem(page, link,
        item.getLevel(), item.isActive(), item.isCurrent(), toLanguageNavigationItems(item.getChildren()), item.getTitle());
  }

  // --- delegated methods ---

  @Override
  public @Nullable String getId() {
    return this.delegate.getId();
  }

  protected NavigationItem newLanguageNavigationItem(@NotNull Page page, @NotNull Link link,
      int level, boolean active, boolean current, @NotNull List<NavigationItem> children, @Nullable String title) {
    return new LanguageNavigationItemV2Impl(page, link,
        level, active, current, children, title,
        getId(), this.componentContext.getComponent());
  }

}
