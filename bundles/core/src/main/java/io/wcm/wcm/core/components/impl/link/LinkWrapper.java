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
package io.wcm.wcm.core.components.impl.link;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.wcm.handler.link.Link;

/**
 * Link wrapper around wcm.io Link.
 */
public final class LinkWrapper implements com.adobe.cq.wcm.core.components.commons.link.Link {

  private final Link link;

  /**
   * @param link wcm.io Link
   */
  public LinkWrapper(@NotNull Link link) {
    this.link = link;
  }

  @Override
  public boolean isValid() {
    return link.isValid();
  }

  @Override
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty("url")
  public @Nullable String getURL() {
    return link.getUrl();
  }

  @Override
  @JsonIgnore
  public @Nullable String getMappedURL() {
    // wcm.io link URLs are always mapped
    return link.getUrl();
  }

  @Override
  @JsonIgnore
  public @Nullable String getExternalizedURL() {
    // wcm.io link URLs are externalized if link handler was called/configured accordingly
    return link.getUrl();
  }

  @Override
  @JsonInclude(Include.NON_EMPTY)
  @JsonSerialize(using = LinkHtmlAttributesSerializer.class)
  @JsonProperty("attributes")
  public @NotNull Map getHtmlAttributes() {
    return link.getAnchorAttributes();
  }

  @Override
  @JsonIgnore
  public @Nullable Object getReference() {
    return ObjectUtils.firstNonNull(link.getTargetPage(), link.getTargetAsset());
  }

  /**
   * @return this or null if link is invalid
   */
  @JsonIgnore
  public @Nullable com.adobe.cq.wcm.core.components.commons.link.Link orNull() {
    if (link.isValid()) {
      return this;
    }
    else {
      return null;
    }
  }

  /**
   * @return wcm.io Link
   */
  @JsonIgnore
  public @NotNull Link getLinkObject() {
    return link;
  }

}
