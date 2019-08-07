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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.url.ui.SiteRoot;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.NavigationItemImpl;

/**
 * wcm.io-based enhancements for {@link Navigation}:
 * <ul>
 * <li>Detect site root via URL handler</li>
 * <li>Build navigation item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Navigation.class, ComponentExporter.class },
    resourceType = NavigationImpl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationImpl implements Navigation {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/navigation/v1/navigation";

  private static final int NO_STRUCTURE_DEPTH = -1;

  @SlingObject
  private Resource resource;
  @AemObject
  private Style currentStyle;
  @AemObject
  private Page currentPage;
  @Self
  private SiteRoot siteRoot;
  @Self
  private LinkHandler linkHandler;
  private List<NavigationItem> items;

  private int structureDepth;
  private boolean skipNavigationRoot;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();
    structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, -1));
    boolean collectAllPages = properties.get(PN_COLLECT_ALL_PAGES, currentStyle.get(PN_COLLECT_ALL_PAGES, true));
    if (collectAllPages) {
      structureDepth = NO_STRUCTURE_DEPTH;
    }
    skipNavigationRoot = properties.get(PN_SKIP_NAVIGATION_ROOT, currentStyle.get(PN_SKIP_NAVIGATION_ROOT, true));
  }

  @Override
  public List<NavigationItem> getItems() {
    if (items == null) {
      items = createItems();
    }
    return items;
  }

  private List<NavigationItem> createItems() {
    List<NavigationItem> result = new ArrayList<>();
    Page rootPage = siteRoot.getRootPage();
    if (rootPage != null) {
      NavigationRoot navigationRoot = new NavigationRoot(rootPage, structureDepth);
      result = getItems(navigationRoot, navigationRoot.page);
      if (!skipNavigationRoot) {
        Link link = linkHandler.get(navigationRoot.page).build();
        boolean isSelected = checkSelected(navigationRoot.page, link);
        NavigationItemImpl root = new NavigationItemImpl(navigationRoot.page, link, isSelected, 0, result);
        result = new ArrayList<>();
        result.add(root);
      }
    }
    else {
      result = Collections.emptyList();
    }
    return result;
  }

  @Override
  public @NotNull String getExportedType() {
    return resource.getResourceType();
  }


  /**
   * Builds the navigation tree for a {@code navigationRoot} page.
   * @param navigationRoot the global navigation tree root (start page)
   * @param subtreeRoot the current sub-tree root (changes depending on the level of recursion)
   * @return the list of collected navigation trees
   */
  private List<NavigationItem> getItems(NavigationRoot navigationRoot, Page subtreeRoot) {
    List<NavigationItem> pages = new ArrayList<>();
    if (navigationRoot.structureDepth == NO_STRUCTURE_DEPTH || subtreeRoot.getDepth() < navigationRoot.structureDepth) {
      Iterator<Page> it = subtreeRoot.listChildren(new PageFilter(false, false));
      while (it.hasNext()) {
        Page page = it.next();
        int pageLevel = page.getDepth();
        int level = pageLevel - navigationRoot.startLevel;
        List<NavigationItem> children = getItems(navigationRoot, page);
        Link link = linkHandler.get(page).build();
        boolean isSelected = checkSelected(page, link);
        if (skipNavigationRoot) {
          level = level - 1;
        }
        pages.add(new NavigationItemImpl(page, link, isSelected, level, children));
      }
    }
    return pages;
  }

  private boolean checkSelected(@NotNull Page page, @NotNull Link link) {
    return StringUtils.equals(currentPage.getPath(), page.getPath()) ||
        StringUtils.startsWith(currentPage.getPath(), page.getPath() + "/") ||
        currentPageIsRedirectTarget(link);
  }

  private boolean currentPageIsRedirectTarget(@NotNull Link link) {
    return link.getTargetPage() != null
        && StringUtils.equals(currentPage.getPath(), link.getTargetPage().getPath());
  }

  private static final class NavigationRoot {

    private final Page page;
    private final int startLevel;
    private final int structureDepth;

    private NavigationRoot(@NotNull Page navigationRoot, int configuredStructureDepth) {
      this.page = navigationRoot;
      this.startLevel = navigationRoot.getDepth();
      this.structureDepth = (configuredStructureDepth >= 0) ? configuredStructureDepth + startLevel : NO_STRUCTURE_DEPTH;
    }
  }

}
