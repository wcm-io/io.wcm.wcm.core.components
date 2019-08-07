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

import static io.wcm.samples.core.testcontext.AppAemContext.ROOT_LEVEL;

import org.apache.sling.api.resource.Resource;

import io.wcm.handler.url.spi.UrlHandlerConfig;

/**
 * URL handler configuration.
 */
class UrlHandlerConfigImpl extends UrlHandlerConfig {

  @Override
  public int getSiteRootLevel(Resource contextResource) {
    return ROOT_LEVEL;
  }

}
