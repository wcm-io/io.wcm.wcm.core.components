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
package io.wcm.wcm.core.components.impl.util;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import io.wcm.handler.link.LinkBuilder;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.MediaBuilder;
import io.wcm.handler.media.MediaHandler;

/**
 * When using the
 * <a href="https://github.com/adobe/aem-core-wcm-components/wiki/Delegation-Pattern-for-Sling-Models">delegation
 * pattern for Sling Models</a> a component that is further up in the inheritance chain gets a resource
 * injects, that has a modified resource type representing the resource type inheritance chain.
 * If this modified resource type is used for looking up content policies it leads to wrong results, as
 * the polices are associated with the resource type stored in the repository, not with the modified one.
 * As the wcm.io Handler make an implicit policy lookup for link and media resolution, we have to make sure
 * to use the unwrapped resource for the processing. This logic is centralized in this class.
 */
public final class HandlerUnwrapper {

  private HandlerUnwrapper() {
    // static methods only
  }

  /**
   * Get {@link MediaBuilder} with unwrapped resource.
   * @param mediaHandler Media handler
   * @param resource Resource
   * @return {@link MediaBuilder} based on unwrapped resource.
   */
  public static @NotNull MediaBuilder get(@NotNull MediaHandler mediaHandler, @NotNull Resource resource) {
    return mediaHandler.get(unwrapResource(resource));
  }

  /**
   * Get {@link LinkBuilder} with unwrapped resource.
   * @param linkHandler Link handler
   * @param resource Resource
   * @return {@link LinkBuilder} based on unwrapped resource.
   */
  public static @NotNull LinkBuilder get(@NotNull LinkHandler linkHandler, @NotNull Resource resource) {
    return linkHandler.get(unwrapResource(resource));
  }

  private static @NotNull Resource unwrapResource(@NotNull Resource resource) {
    // do not unwrap CoreResourceWrapper which is used for embedding components into each other
    if (resource instanceof CoreResourceWrapper) {
      return resource;
    }
    return ResourceUtil.unwrap(resource);
  }

}
