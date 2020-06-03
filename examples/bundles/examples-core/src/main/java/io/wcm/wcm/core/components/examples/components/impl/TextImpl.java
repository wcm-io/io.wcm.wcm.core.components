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
package io.wcm.wcm.core.components.examples.components.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Text;

import io.wcm.wcm.core.components.examples.services.impl.CoreComponentVersionService;

/**
 * Customizes text model that replaced version placeholders with the core component bundle versions.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Text.class, ComponentExporter.class },
    resourceType = { TextImpl.RESOURCE_TYPE })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TextImpl implements Text {

  static final String RESOURCE_TYPE = "wcm-io-samples/wcm-core-components/components/versionText";

  private static final String PLACEHOLDERS_PROJECT_VERSION = "${project.version}";
  private static final String PLACEHOLDERS_CORE_COMPONENTS_VERSION = "${core.wcm.components.version}";

  private static final Pattern DOUBLE_VERSION_PATTERN = Pattern.compile("^(\\d+\\.\\d+\\.\\d+)\\.(\\d+\\.\\d+\\.\\d+.*)$");

  @SlingObject
  private Resource resource;

  @Self
  @Via(type = ResourceSuperType.class)
  private Text delegate;

  @OSGiService
  private CoreComponentVersionService versionService;

  @Override
  public String getText() {
    return replacePlaceholder(this.delegate.getText());
  }

  @Override
  public boolean isRichText() {
    return this.delegate.isRichText();
  }

  @Override
  public @NotNull String getExportedType() {
    return resource.getResourceType();
  }

  private @NotNull String replacePlaceholder(@NotNull String markup) {
    String result = StringUtils.replace(markup, PLACEHOLDERS_PROJECT_VERSION, formatVersion(versionService.getProjectVersion()));
    return StringUtils.replace(result, PLACEHOLDERS_CORE_COMPONENTS_VERSION, formatVersion(versionService.getCoreComponentVersion()));
  }

  private String formatVersion(@Nullable String version) {
    String formattedVersion = StringUtils.defaultString(version);
    formattedVersion = StringUtils.replace(formattedVersion, "_", ".");

    Matcher matcher = DOUBLE_VERSION_PATTERN.matcher(formattedVersion);
    if (matcher.matches()) {
      formattedVersion = matcher.group(1) + "-" + matcher.group(2);
    }

    return formattedVersion;
  }

}
