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
package io.wcm.wcm.core.components.impl.models.helpers;

import static io.wcm.wcm.core.components.testcontext.AppAemContext.LANGUAGE_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;

import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;

public final class DataLayerTestUtils {

  private DataLayerTestUtils() {
    // static methods only
  }

  /**
   * Sets the data layer context aware configuration of the AEM test context to enabled/disabled
   * @param context The AEM test context
   * @param enabled {@code true} to enable the data layer, {@code false} to disable it
   */
  public static void enableDataLayer(AemContext context, boolean enabled) {
    MockContextAwareConfig.writeConfiguration(context, LANGUAGE_ROOT,
        DataLayerConfig.class, "enabled", enabled);
  }

  /**
   * Formats a calendar value using the same format as the data layer.
   * @param calendar Calendar value
   * @return Date string
   */
  public static String formatDate(Calendar calendar) {
    DateFormat timeStampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    timeStampDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return timeStampDateFormat.format(calendar.getTime());
  }

  @SuppressWarnings("unchecked")
  public static void assertNavigationItems_DataLayer(Collection<NavigationItem> items, Page... pages) {
    assertListItems_DataLayer((Collection)items, pages);
  }

  public static void assertListItems_DataLayer(Collection<ListItem> items, Page... pages) {
    List<String> expected = Arrays.stream(pages)
        .map(Page::getPath)
        .collect(Collectors.toList());
    List<String> actual = items.stream()
        .map(ListItem::getPath)
        .collect(Collectors.toList());
    assertEquals(expected, actual);

    List<ListItem> itemsList = List.copyOf(items);
    for (int i = 0; i < items.size(); i++) {
      Page page = pages[i];
      ListItem item = itemsList.get(i);
      ComponentData data = item.getData();
      assertNotNull(data, "Item #" + i + " componentData");
      assertNotNull(data.getType(), "Item #" + i + " type");
      assertEquals(page.getPath() + ".html", data.getLinkUrl(), "Item #" + i + " linkUrl");
      assertEquals(page.getTitle(), data.getTitle(), "Item #" + i + " title");
    }
  }

}
