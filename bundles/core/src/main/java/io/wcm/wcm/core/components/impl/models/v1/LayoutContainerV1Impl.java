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
package io.wcm.wcm.core.components.impl.models.v1;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ContainerItem;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractContainerImpl;

/**
 * wcm.io-based enhancements for {@link LayoutContainer}:
 * <ul>
 * <li>Build background image URL using Media handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { LayoutContainer.class, ComponentExporter.class },
    resourceType = LayoutContainerV1Impl.RESOURCE_TYPE)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class LayoutContainerV1Impl extends AbstractContainerImpl implements LayoutContainer {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/container/v1/container";

  @Self
  @Via(type = ResourceSuperType.class)
  private LayoutContainer delegate;

  // --- delegated methods ---

  @Override
  @SuppressWarnings("null")
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  public @NotNull String getId() {
    return this.delegate.getId();
  }

  /**
   * @deprecated since 1.13.0-2.22.6- use {@link #getChildren()}
   */
  @Override
  @JsonIgnore
  @Deprecated(since = "1.13.0-2.22.6")
  public @NotNull List<ListItem> getItems() {
    return delegate.getItems();
  }

  @Override
  public @NotNull List<? extends ContainerItem> getChildren() {
    return delegate.getChildren();
  }

  @Override
  public @NotNull Map<String, ? extends ComponentExporter> getExportedItems() {
    return delegate.getExportedItems();
  }

  @Override
  public @NotNull String[] getExportedItemsOrder() {
    return delegate.getExportedItemsOrder();
  }

  @Override
  public @NotNull LayoutType getLayout() {
    return delegate.getLayout();
  }

  @Override
  @JsonIgnore
  @SuppressWarnings({ "PMD.ReturnEmptyCollectionRatherThanNull", "java:S1168" })
  public String[] getDataLayerShownItems() {
    return null;
  }

}
