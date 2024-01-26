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
package io.wcm.wcm.core.components.impl.models.helpers;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

import io.wcm.handler.link.Link;

/**
 * {@link LanguageNavigationItem} implementation.
 */
@SuppressWarnings("java:S110") // class hierarchy levels
public class LanguageNavigationItemV2Impl extends NavigationItemV2Impl implements LanguageNavigationItem {

  private final Page page;
  private final String title;
  private Locale locale;
  private String country;
  private String language;

  /**
   * @param page Page
   * @param link Link
   * @param level Level
   * @param active Active
   * @param current Current
   * @param children Children
   * @param title Title
   * @param parentId Parent ID
   * @param parentComponent The component that contains this list item
   */
  @SuppressWarnings("java:S107") // number of parameters
  public LanguageNavigationItemV2Impl(@NotNull Page page, @NotNull Link link,
      int level, boolean active, boolean current, @NotNull List<NavigationItem> children,
      @Nullable String title, @Nullable String parentId, @Nullable Component parentComponent) {
    super(page, link, level, active, current, children, parentId, parentComponent);
    this.page = page;
    this.title = title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public Locale getLocale() {
    if (locale == null) {
      // looks up jcr:language properties to the root, then considers the page name, falls back to system default
      // we therefore assume the language structure is correctly configured for the site for this to be accurate
      locale = page.getLanguage(false);
    }
    return locale;
  }

  @Override
  public String getCountry() {
    if (country == null) {
      country = page.getLanguage(false).getCountry();
    }
    return country;
  }

  @Override
  public String getLanguage() {
    if (language == null) {
      // uses hyphens to ensure it's hreflang valid
      language = page.getLanguage(false).toString().replace('_', '-');
    }
    return language;
  }

  @Override
  @NotNull
  protected final PageData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asPage()
        .withLanguage(this::getLanguage)
        .build();
  }

}
