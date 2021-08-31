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
package io.wcm.wcm.core.components.impl.models.v2;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Text;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;

import io.wcm.handler.richtext.RichTextHandler;
import io.wcm.handler.richtext.TextMode;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;

/**
 * wcm.io-based enhancements for {@link Text}:
 * <ul>
 * <li>Build rich text markup using Rich Text Handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Text.class, ComponentExporter.class },
    resourceType = TextV2Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TextV2Impl extends AbstractComponentImpl implements Text {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/text/v2/text";

  @Self
  private RichTextHandler richTextHandler;

  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private boolean textIsRich;
  private String text;

  @PostConstruct
  private void activate() {
    text = richTextHandler
        .get(resource)
        .textMode(textIsRich ? TextMode.XHTML : TextMode.PLAIN)
        .buildMarkup();
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public boolean isRichText() {
    return textIsRich;
  }

  // --- data layer ---

  @Override
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asComponent()
        .withText(() -> StringUtils.defaultIfEmpty(this.getText(), StringUtils.EMPTY))
        .build();
  }

}
