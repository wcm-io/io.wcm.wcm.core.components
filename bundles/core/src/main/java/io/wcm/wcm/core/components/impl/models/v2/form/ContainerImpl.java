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
package io.wcm.wcm.core.components.impl.models.v2.form;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.form.Container;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.LinkHandler;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentExporterImpl;

/**
 * wcm.io-based enhancements for {@link Container}:
 * <ul>
 * <li>Generate form action and redirect URLs using link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Container.class, ContainerExporter.class, ComponentExporter.class },
    resourceType = ContainerImpl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContainerImpl extends AbstractComponentExporterImpl implements Container {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/form/container/v2/container";

  @Self
  @Via(type = ResourceSuperType.class)
  private Container delegate;

  @Self
  private LinkHandler linkHandler;
  @AemObject
  private Page currentPage;

  private String action;
  private String redirect;

  @PostConstruct
  private void initModel() {
    this.action = linkHandler.get(currentPage).buildUrl();

    String redirectPath = resource.getValueMap().get("redirect", String.class);
    this.redirect = linkHandler.get(redirectPath).buildUrl();
  }

  @Override
  public String getAction() {
    return action;
  }

  @Override
  public String getRedirect() {
    return redirect;
  }

  // --- delegated methods ---

  @Override
  public String getMethod() {
    return this.delegate.getMethod();
  }

  @Override
  public String getId() {
    return this.delegate.getId();
  }

  @Override
  public String getName() {
    return this.delegate.getName();
  }

  @Override
  public String getEnctype() {
    return this.delegate.getEnctype();
  }

  @Override
  public String getResourceTypeForDropArea() {
    return this.delegate.getResourceTypeForDropArea();
  }

  @Override
  public @NotNull String[] getExportedItemsOrder() {
    return this.delegate.getExportedItemsOrder();
  }

  @Override
  public @NotNull Map<String, ? extends ComponentExporter> getExportedItems() {
    return this.delegate.getExportedItems();
  }

  @Override
  public @Nullable String[] getErrorMessages() {
    return this.delegate.getErrorMessages();
  }

}
