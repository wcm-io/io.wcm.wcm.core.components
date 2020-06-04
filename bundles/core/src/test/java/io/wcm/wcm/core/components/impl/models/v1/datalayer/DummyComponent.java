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
package io.wcm.wcm.core.components.impl.models.v1.datalayer;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;

@Model(adaptables = SlingHttpServletRequest.class)
public class DummyComponent extends AbstractComponentImpl {

  @Self
  private LinkHandler linkHandler;
  @Self
  private MediaHandler mediaHandler;

  @Override
  public String getDataLayerTitle() {
    return resource.getValueMap().get(JCR_TITLE, String.class);
  }

  @Override
  public String getDataLayerDescription() {
    return resource.getValueMap().get(JCR_DESCRIPTION, String.class);
  }

  @Override
  public String getDataLayerText() {
    return resource.getValueMap().get("text", String.class);
  }

  @Override
  public Link getDataLayerLink() {
    return linkHandler.get(resource).build();
  }

  @Override
  public Media getDataLayerMedia() {
    return mediaHandler.get(resource).build();
  }

}
