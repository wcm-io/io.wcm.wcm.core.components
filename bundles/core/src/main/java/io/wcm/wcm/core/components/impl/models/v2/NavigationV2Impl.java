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

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.url.UrlHandler;
import io.wcm.handler.url.ui.SiteRoot;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.NavigationItemV2Impl;

/**
 * wcm.io-based enhancements for {@link Navigation}:
 * <ul>
 * <li>Detect site root via URL handler</li>
 * <li>Build navigation item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Navigation.class, ComponentExporter.class },
    resourceType = NavigationV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationV2Impl extends AbstractComponentImpl implements Navigation {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/navigation/v2/navigation";

  private static final int NO_STRUCTURE_DEPTH = -1;

  @AemObject
  private Style currentStyle;
  @AemObject
  private PageManager pageManager;
  @SlingObject
  private ResourceResolver resourceResolver;
  @Self
  private SiteRoot siteRoot;
  @Self
  private UrlHandler urlHandler;
  @Self
  private LinkHandler linkHandler;
  private List<NavigationItem> items;

  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private @Nullable String accessibilityLabel;

  private String navigationRootPath;
  private int structureDepth;
  private int structureStart;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();
    navigationRootPath = resource.getValueMap().get(PN_NAVIGATION_ROOT, currentStyle.get(PN_NAVIGATION_ROOT, String.class));
    structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, NO_STRUCTURE_DEPTH));
    boolean collectAllPages = properties.get(PN_COLLECT_ALL_PAGES, currentStyle.get(PN_COLLECT_ALL_PAGES, true));
    if (collectAllPages) {
      structureDepth = NO_STRUCTURE_DEPTH;
    }

    if (currentStyle.containsKey(PN_STRUCTURE_START) || properties.containsKey(PN_STRUCTURE_START)) {
      structureStart = properties.get(PN_STRUCTURE_START, currentStyle.get(PN_STRUCTURE_START, 1));
    }
    else {
      @SuppressWarnings("deprecation")
      boolean skipNavigationRoot = properties.get(PN_SKIP_NAVIGATION_ROOT, currentStyle.get(PN_SKIP_NAVIGATION_ROOT, true));
      if (skipNavigationRoot) {
        structureStart = 1;
      }
      else {
        structureStart = 0;
      }
    }
  }

  @Override
  public List<NavigationItem> getItems() {
    if (items == null) {
      items = createItems();
    }
    return items;
  }

  @Override
  public String getAccessibilityLabel() {
    return accessibilityLabel;
  }

  private List<NavigationItem> createItems() {
    List<NavigationItem> result = new ArrayList<>();
    Page rootPage = getNavigationRootPage();
    if (rootPage != null) {
      NavigationRoot navigationRoot = new NavigationRoot(rootPage, structureDepth);
      result = getNavigationTree(navigationRoot);
    }
    else {
      result = Collections.emptyList();
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Get root page for navigation.
   * @return Root page or null if detection was not possible
   */
  private @Nullable Page getNavigationRootPage() {
    Page navigationRootPage = null;
    if (StringUtils.isBlank(navigationRootPath)) {
      // use site root as configured in URL handler as navigation root
      navigationRootPage = siteRoot.getRootPage();
    }
    else if (StringUtils.startsWith(navigationRootPath, "/")) {
      // configured root path is absolute path - rewrite to current context and try to resolve matching page
      Resource configuredRootResource = new SyntheticResource(resourceResolver, navigationRootPath, NT_UNSTRUCTURED);
      String rewrittenPath = urlHandler.rewritePathToContext(configuredRootResource);
      navigationRootPage = pageManager.getPage(rewrittenPath);
    }
    else {
      // configured root path is relative path - try to resolve relative to site root path
      String siteRootPath = siteRoot.getRootPath();
      if (siteRootPath != null) {
        navigationRootPage = pageManager.getPage(siteRootPath + "/" + navigationRootPath);
      }
    }
    return navigationRootPage;
  }

  /**
   * Build navigation tree respecting the configured structure start level.
   * @param navigationRoot Navigation root
   * @return Navigation item
   */
  private List<NavigationItem> getNavigationTree(NavigationRoot navigationRoot) {
    List<NavigationItem> itemTree = new ArrayList<>();
    List<NavigationRoot> rootItems = getRootItems(navigationRoot);
    for (NavigationRoot rootItem : rootItems) {
      itemTree.addAll(getItems(rootItem, rootItem.page));
    }
    if (structureStart == 0) {
      Link link = linkHandler.get(navigationRoot.page).build();
      boolean active = isActive(navigationRoot.page, link);
      boolean current = isCurrent(navigationRoot.page, link);
      NavigationItem root = newNavigationItem(navigationRoot.page, link, 0, active, current, itemTree);
      itemTree = new ArrayList<>();
      itemTree.add(root);
    }
    return itemTree;
  }

  /**
   * Get navigation root items for the configured structure start.
   * @param navigationRoot Navigation root.
   * @return Navigation root items
   */
  private List<NavigationRoot> getRootItems(NavigationRoot navigationRoot) {
    LinkedList<NavigationRoot> rootItems = new LinkedList<>();
    rootItems.addLast(navigationRoot);
    if (structureStart > 0) {
      int level = 1;
      while (level != structureStart && !rootItems.isEmpty()) {
        int size = rootItems.size();
        while (size > 0) {
          NavigationRoot item = rootItems.removeFirst();
          Iterator<Page> it = item.page.listChildren(new PageFilter());
          while (it.hasNext()) {
            rootItems.addLast(new NavigationRoot(it.next(), structureDepth));
          }
          size = size - 1;
        }
        level = level + 1;
      }
    }
    return rootItems;
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
        boolean active = isActive(page, link);
        boolean current = isCurrent(page, link);
        if (structureStart == 0) {
          level = level - 1;
        }
        pages.add(newNavigationItem(page, link, level, active, current, children));
      }
    }
    return pages;
  }

  private boolean isActive(@NotNull Page page, @NotNull Link link) {
    return isCurrent(page, link) ||
        StringUtils.startsWith(getCurrentPage().getPath(), page.getPath() + "/");
  }

  private boolean isCurrent(@NotNull Page page, @NotNull Link link) {
    return StringUtils.equals(page.getPath(), getCurrentPage().getPath()) ||
        currentPageIsRedirectTarget(link);
  }

  private boolean currentPageIsRedirectTarget(@NotNull Link link) {
    return link.getTargetPage() != null
        && StringUtils.equals(getCurrentPage().getPath(), link.getTargetPage().getPath());
  }

  protected NavigationItem newNavigationItem(@NotNull Page page, @NotNull Link link,
      int level, boolean active, boolean current, @NotNull List<NavigationItem> children) {
    return new NavigationItemV2Impl(page, link, level, active, current, children,
        getId(), getParentComponent());
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
