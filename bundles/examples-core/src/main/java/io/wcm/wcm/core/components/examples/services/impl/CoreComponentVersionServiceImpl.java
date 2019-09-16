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
package io.wcm.wcm.core.components.examples.services.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of {@link CoreComponentVersionService}.
 */
@Component(service = CoreComponentVersionService.class)
public class CoreComponentVersionServiceImpl implements CoreComponentVersionService {

  private String projectVersion;
  private String coreComponentVersion;

  @Activate
  private void activate(BundleContext bundleContext) {
    projectVersion = getBundleVersion(bundleContext, "io.wcm.wcm.core.components");
    coreComponentVersion = getBundleVersion(bundleContext, "com.adobe.cq.core.wcm.components.core");
  }

  private @Nullable String getBundleVersion(BundleContext bundleContext, String symbolicName) {
    return Arrays.stream(bundleContext.getBundles())
        .filter(bundle -> StringUtils.equals(bundle.getSymbolicName(), symbolicName))
        .map(Bundle::getVersion)
        .map(Version::toString)
        .findFirst().orElse(null);
  }

  @Override
  public String getProjectVersion() {
    return projectVersion;
  }

  @Override
  public String getCoreComponentVersion() {
    return coreComponentVersion;
  }

}
