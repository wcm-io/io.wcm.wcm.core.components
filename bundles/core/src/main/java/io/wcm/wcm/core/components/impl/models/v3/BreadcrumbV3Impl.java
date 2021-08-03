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
package io.wcm.wcm.core.components.impl.models.v3;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.url.ui.SiteRoot;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;

/**
 * wcm.io-based enhancements for {@link Breadcrumb}:
 * <ul>
 * <li>Detect site root via URL handler</li>
 * <li>Build navigation item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Breadcrumb.class, ComponentExporter.class },
    resourceType = BreadcrumbV3Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class BreadcrumbV3Impl extends AbstractComponentImpl implements Breadcrumb {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/breadcrumb/v3/breadcrumb";

  @AemObject
  private Style currentStyle;
  @Self
  private SiteRoot siteRoot;
  @Self
  private LinkHandler linkHandler;
  private List<NavigationItem> items;

  private boolean showHidden;
  private boolean hideCurrent;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();
    showHidden = properties.get(PN_SHOW_HIDDEN, currentStyle.get(PN_SHOW_HIDDEN, false));
    hideCurrent = properties.get(PN_HIDE_CURRENT, currentStyle.get(PN_HIDE_CURRENT, false));
  }

  @Override
  public Collection<NavigationItem> getItems() {
    if (items == null) {
      items = createItems();
    }
    return Collections.unmodifiableList(items);
  }

  private List<NavigationItem> createItems() {
    List<NavigationItem> result = new LinkedList<>();
    Page page = currentPage;
    while (page != null) {
      boolean isCurrentPage = StringUtils.equals(page.getPath(), currentPage.getPath());
      if (!(isCurrentPage && hideCurrent)) {
        if (checkIfNotHidden(page)) {
          Link link = linkHandler.get(page).build();
          NavigationItem navigationItem = newNavigationItem(page, link, isCurrentPage);
          result.add(0, navigationItem);
        }
      }
      if (siteRoot.isRootPage(page)) {
        break;
      }
      page = page.getParent();
    }
    return result;
  }

  private boolean checkIfNotHidden(Page page) {
    return !page.isHideInNav() || showHidden;
  }

  protected NavigationItem newNavigationItem(@NotNull Page page, @NotNull Link link, boolean current) {
    return new BreadcrumbV3ItemImpl(page, link, page.getDepth(),
        current, Collections.emptyList(),
        getId(), this.componentContext.getComponent());
  }

}
