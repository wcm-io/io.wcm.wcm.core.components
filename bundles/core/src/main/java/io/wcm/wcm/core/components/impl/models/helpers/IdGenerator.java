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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.components.ComponentContext;

/**
 * Helps generating component IDs.
 */
public final class IdGenerator {

  /**
   * Name of the separator character used between prefix and hash when generating an ID, e.g. image-5c7e0ef90d
   */
  public static final String ID_SEPARATOR = "-";

  private IdGenerator() {
    // static methods only
  }

  /**
   * Returns an ID based on the prefix, the ID_SEPARATOR and a hash of the path, e.g. image-5c7e0ef90d
   * <p>
   * code taken over from {@link com.adobe.cq.wcm.core.components.internal.Utils}
   * </p>
   * @param prefix the prefix for the ID
   * @param path the resource path
   * @return the generated ID
   */
  // code taken over from https://github.com/adobe/aem-core-wcm-components/blob/master/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/Utils.java
  public static String generateId(@NotNull String prefix, @NotNull String path) {
    return StringUtils.join(prefix, ID_SEPARATOR, StringUtils.substring(DigestUtils.sha256Hex(path), 0, 10));
  }

  /**
   * Returns an auto generated component ID.
   * The ID is the first 10 characters of an SHA-1 hexadecimal hash of the component path,
   * prefixed with the component name. Example: title-810f3af321
   * If the component is referenced, the path is taken to be a concatenation of the component path,
   * with the path of the first parent context resource that exists on the page or in the template.
   * This ensures the ID is unique if the same component is referenced multiple times on the same page or template.
   * Collision
   * ---------
   * c = expected collisions
   * c ~= (i^2)/(2m) - where i is the number of items and m is the number of possibilities for each item.
   * m = 16^n - for a hexadecimal string, where n is the number of characters.
   * For i = 1000 components with the same name, and n = 10 characters:
   * c ~= (1000^2)/(2*(16^10))
   * c ~= 0.00000045
   * <p>
   * code taken over from {@link com.adobe.cq.wcm.core.components.internal.models.v1.AbstractComponentImpl}
   * </p>
   * @return the auto generated component ID
   */
  public static String generateIdForComponent(@NotNull Resource resource,
      @Nullable Page currentPage, @Nullable ComponentContext componentContext) {
    String resourceType = resource.getResourceType();
    String prefix = StringUtils.substringAfterLast(resourceType, "/");
    String path = resource.getPath();

    if (currentPage != null && componentContext != null) {
      PageManager pageManager = currentPage.getPageManager();
      Page containingPage = pageManager.getContainingPage(resource);
      Template template = currentPage.getTemplate();
      boolean inCurrentPage = (containingPage != null && StringUtils.equals(containingPage.getPath(), currentPage.getPath()));
      boolean inTemplate = (template != null && path.startsWith(template.getPath()));
      if (!inCurrentPage && !inTemplate) {
        ComponentContext parentContext = componentContext.getParent();
        while (parentContext != null) {
          Resource parentContextResource = parentContext.getResource();
          if (parentContextResource != null) {
            Page parentContextPage = pageManager.getContainingPage(parentContextResource);
            inCurrentPage = (parentContextPage != null && StringUtils.equals(parentContextPage.getPath(), currentPage.getPath()));
            inTemplate = (template != null && parentContextResource.getPath().startsWith(template.getPath()));
            if (inCurrentPage || inTemplate) {
              path = parentContextResource.getPath().concat(resource.getPath());
              break;
            }
          }
          parentContext = parentContext.getParent();
        }
      }

    }

    return generateId(prefix, path);
  }

}
