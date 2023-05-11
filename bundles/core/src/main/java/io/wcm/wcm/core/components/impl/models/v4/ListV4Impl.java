/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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
package io.wcm.wcm.core.components.impl.models.v4;

import java.text.Collator;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.wcm.core.components.impl.link.LinkWrapper;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.LinkListItemV2Impl;
import io.wcm.wcm.core.components.impl.models.helpers.PageListItemV4Impl;

/**
 * wcm.io-based enhancements for {@link List}:
 * <ul>
 * <li>Build link item links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { List.class, ComponentExporter.class },
    resourceType = ListV4Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListV4Impl extends AbstractComponentImpl implements List {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/list/v4/list";

  static final String SOURCE_STATIC = "static";
  static final String ORDERBY_TITLE = "title";
  static final String ORDERBY_MODIFIED = "modified";
  static final String SORTORDER_DESC = "desc";

  private Collection<ListItem> staticListItems;

  @Self
  @Via(type = ResourceSuperType.class)
  private List delegate;

  @Self
  private LinkHandler linkHandler;

  @Override
  @JsonProperty("items")
  public @NotNull Collection<ListItem> getListItems() {
    if (this.staticListItems != null) {
      return staticListItems;
    }
    if (isListSourceStatic()) {
      Resource staticItems = this.resource.getChild(NN_STATIC);
      if (staticItems != null) {
        this.staticListItems = getStaticListItems(staticItems);
        return this.staticListItems;
      }
    }
    return transformToPageListItems(getItems());
  }

  // --- delegated methods ---

  @Override
  @SuppressWarnings("null")
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  public @NotNull String getId() {
    return this.delegate.getId();
  }

  @Override
  @SuppressWarnings("deprecation")
  public Collection<Page> getItems() {
    return this.delegate.getItems();
  }

  @Override
  public boolean linkItems() {
    return this.delegate.linkItems();
  }

  @Override
  public boolean showDescription() {
    return this.delegate.showDescription();
  }

  @Override
  public boolean showModificationDate() {
    return this.delegate.showModificationDate();
  }

  @Override
  public String getDateFormatString() {
    return this.delegate.getDateFormatString();
  }

  @Override
  public boolean displayItemAsTeaser() {
    return this.delegate.displayItemAsTeaser();
  }

  // --- internal methods ---

  private boolean isListSourceStatic() {
    String source = resource.getValueMap().get(PN_SOURCE, String.class);
    return StringUtils.equals(source, SOURCE_STATIC);
  }

  private Collection<ListItem> getStaticListItems(@NotNull Resource staticItemsResource) {
    Stream<Resource> itemResources = StreamSupport.stream(staticItemsResource.getChildren().spliterator(), false);
    Stream<ListItem> listItems = itemResources
        .map(this::toLinkListItem)
        .filter(Objects::nonNull);

    // apply sorting
    ValueMap properties = resource.getValueMap();
    String orderBy = properties.get(PN_ORDER_BY, String.class);
    String sortOrder = properties.get(PN_SORT_ORDER, String.class);
    int direction = StringUtils.equalsIgnoreCase(sortOrder, SORTORDER_DESC) ? -1 : 1;
    if (StringUtils.equals(orderBy, ORDERBY_TITLE)) {
      // getTitle may return null, define null to be greater than nonnull values
      Comparator<String> titleComparator = Comparator.nullsLast(getCollator());
      listItems = listItems.sorted((item1, item2) -> direction * titleComparator.compare(item1.getTitle(), item2.getTitle()));
    }
    else if (StringUtils.equals(orderBy, ORDERBY_MODIFIED)) {
      // getLastModified may return null, define null to be after nonnull values
      listItems = listItems.sorted((item1, item2) -> direction * ObjectUtils.compare(getLastModifiedDate(item1),
          getLastModifiedDate(item2), true));
    }

    return listItems.collect(Collectors.toList());
  }

  private Collator getCollator() {
    Locale locale = Locale.US;
    Page currentPage = getCurrentPage();
    if (currentPage != null) {
      locale = currentPage.getLanguage();
    }
    Collator collator = Collator.getInstance(locale);
    collator.setStrength(Collator.PRIMARY);
    return collator;
  }

  private Calendar getLastModifiedDate(ListItem item) {
    if (item instanceof PageListItemV4Impl) {
      return ((PageListItemV4Impl)item).getPage().getLastModified();
    }
    return null;
  }

  @SuppressWarnings("null")
  private @Nullable ListItem toLinkListItem(@NotNull Resource itemResource) {
    Link link = linkHandler.get(itemResource).build();
    if (!link.isValid()) {
      return null;
    }
    String linkText = itemResource.getValueMap().get(List.PN_LINK_TEXT, String.class);
    if (StringUtils.isBlank(linkText)) {
      linkText = link.getUrl();
    }
    if (link.getTargetPage() != null) {
      return newPageListItem(link.getTargetPage(), link, linkText);
    }
    else {
      return new LinkListItemV2Impl(linkText, new LinkWrapper(link), "item", getId(), getParentComponent(), this.resource);
    }
  }

  protected Collection<ListItem> transformToPageListItems(Collection<Page> pages) {
    return pages.stream()
        .filter(Objects::nonNull)
        .map(page -> newPageListItem(page, linkHandler.get(page).build(), null))
        .collect(Collectors.toList());
  }

  protected ListItem newPageListItem(@NotNull Page page, @NotNull Link link, @Nullable String linkText) {
    return new PageListItemV4Impl(page, link,
        getId(), getParentComponent(), showDescription(), linkItems() || displayItemAsTeaser(), linkText);
  }

}
