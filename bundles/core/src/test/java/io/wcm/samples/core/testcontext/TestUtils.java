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
package io.wcm.samples.core.testcontext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * Test helpers.
 */
public final class TestUtils {

  private TestUtils() {
    // static methods only
  }

  public static void loadComponentDefinition(AemContext context, String resourceType) {
    String relativePath = StringUtils.substringAfter(resourceType, "wcm-io/wcm/core/");
    try (InputStream is = new FileInputStream("src/main/webapp/app-root/" + relativePath + ".json")) {
      context.load().json(is, "/apps/" + resourceType);
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to load component definition for " + resourceType, ex);
    }
  }

  public static void assertValidLink(Object object, String href) {
    assertTrue(object instanceof LinkMixin, "is LinkMixin");
    LinkMixin linkMixin = (LinkMixin)object;
    assertTrue(linkMixin.isLinkValid());
    assertEquals(ImmutableMap.of("href", href), linkMixin.getLinkAttributes());
  }

  public static void assertValidLink(Object object, String href, String target) {
    assertTrue(object instanceof LinkMixin, "is LinkMixin");
    LinkMixin linkMixin = (LinkMixin)object;
    assertTrue(linkMixin.isLinkValid());
    assertEquals(ImmutableMap.of("href", href, "target", target), linkMixin.getLinkAttributes());
  }

  public static void assertInvalidLink(Object object) {
    assertTrue(object instanceof LinkMixin, "is LinkMixin");
    LinkMixin linkMixin = (LinkMixin)object;
    assertFalse(linkMixin.isLinkValid());
    assertNull(linkMixin.getLinkAttributes());
  }

  public static void assertValidMedia(Object object, String mediaUrl) {
    assertTrue(object instanceof MediaMixin, "is MediaMixin");
    MediaMixin mediaMixin = (MediaMixin)object;
    assertTrue(mediaMixin.isMediaValid());
    assertEquals(mediaUrl, mediaMixin.getMediaObject().getUrl());
  }

  public static void assertInvalidMedia(Object object) {
    assertTrue(object instanceof MediaMixin, "is MediaMixin");
    MediaMixin mediaMixin = (MediaMixin)object;
    assertFalse(mediaMixin.isMediaValid());
    assertNull(mediaMixin.getMediaMarkup());
  }

  @SuppressWarnings("unchecked")
  public static void assertNavigationItems(Collection<NavigationItem> items, Page... pages) {
    assertListItems((Collection)items, pages);
  }

  public static void assertListItems(Collection<ListItem> items, Page... pages) {
    List<String> expected = Arrays.stream(pages)
        .map(Page::getPath)
        .collect(Collectors.toList());
    List<String> actual = items.stream()
        .map(ListItem::getPath)
        .collect(Collectors.toList());
    assertEquals(expected, actual);

    for (ListItem item : items) {
      assertTrue(item instanceof LinkMixin, item.getPath() + " is LinkMixin");
    }
  }

}
