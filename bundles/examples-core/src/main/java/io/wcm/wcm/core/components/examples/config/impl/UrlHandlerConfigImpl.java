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
package io.wcm.wcm.core.components.examples.config.impl;

import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;

import io.wcm.handler.url.spi.UrlHandlerConfig;

/**
 * URL handler configuration.
 */
@Component(service = UrlHandlerConfig.class)
public class UrlHandlerConfigImpl extends UrlHandlerConfig {

  @Override
  public int getSiteRootLevel(Resource contextResource) {
    return 2;
  }

}
