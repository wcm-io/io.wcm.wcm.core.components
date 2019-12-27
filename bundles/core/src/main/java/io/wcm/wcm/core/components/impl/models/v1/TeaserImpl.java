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

import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.handler.media.MediaNameConstants.PROP_CSS_CLASS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.richtext.RichTextHandler;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentExporterImpl;
import io.wcm.wcm.core.components.impl.models.helpers.LinkListItemImpl;
import io.wcm.wcm.core.components.models.mixin.LinkMixin;
import io.wcm.wcm.core.components.models.mixin.MediaMixin;

/**
 * wcm.io-based enhancements for {@link Teaser}:
 * <ul>
 * <li>Build image using Media handler</li>
 * <li>Build links using Link handler</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { Teaser.class, ComponentExporter.class },
    resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends AbstractComponentExporterImpl implements Teaser, MediaMixin, LinkMixin {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/teaser/v1/teaser";

  @AemObject
  private Style currentStyle;
  @Self
  private MediaHandler mediaHandler;
  @Self
  private LinkHandler linkHandler;
  @Self
  private RichTextHandler richTextHandler;

  private Media media;
  private Link link;
  private List<ListItem> actions = new ArrayList<>();
  private Page targetPage;

  private String title;
  private String description;
  private String titleType;
  private boolean actionsEnabled;
  private boolean titleHidden;
  private boolean descriptionHidden;
  private boolean imageLinkHidden;
  private boolean titleLinkHidden;
  private boolean titleFromPage;
  private boolean descriptionFromPage;

  @PostConstruct
  private void activate() {
    ValueMap properties = resource.getValueMap();

    // read style properties
    titleHidden = currentStyle.get(PN_TITLE_HIDDEN, false);
    descriptionHidden = currentStyle.get(PN_DESCRIPTION_HIDDEN, false);
    titleType = currentStyle.get(PN_TITLE_TYPE, (String)null);
    imageLinkHidden = currentStyle.get(PN_IMAGE_LINK_HIDDEN, false);
    titleLinkHidden = currentStyle.get(PN_TITLE_LINK_HIDDEN, false);
    boolean actionsDisabled = currentStyle.get(PN_ACTIONS_DISABLED, false);

    // read component properties
    actionsEnabled = properties.get(PN_ACTIONS_ENABLED, false) && !actionsDisabled;
    titleFromPage = properties.get(PN_TITLE_FROM_PAGE, false);
    descriptionFromPage = properties.get(PN_DESCRIPTION_FROM_PAGE, false);


    // resolve teaser media
    media = mediaHandler.get(resource)
        .property(PROP_CSS_CLASS, "cmp-image__image")
        .build();

    // resolve actions with links
    if (actionsEnabled) {
      Resource actionsNode = resource.getChild(NN_ACTIONS);
      if (actionsNode != null) {
        for (Resource actionResource : actionsNode.getChildren()) {
          String actionTitle = actionResource.getValueMap().get(PN_ACTION_TEXT, String.class);
          Link actionLink = linkHandler.get(actionResource).build();
          if (actionTitle != null && actionLink.isValid()) {
            actions.add(new LinkListItemImpl(actionTitle, actionLink));
            if (targetPage == null) {
              // get target page from first action
              targetPage = actionLink.getTargetPage();
            }
          }
        }
      }
      // primary link is not enabled when actions are enabled
      link = linkHandler.invalid();
    }

    // if no actions enabled, resolve primary teaser link
    else {
      link = linkHandler.get(resource).build();
      targetPage = link.getTargetPage();
    }

    // read title and description
    if (!titleHidden) {
      if (titleFromPage) {
        if (targetPage != null) {
          title = StringUtils.defaultIfEmpty(targetPage.getPageTitle(), targetPage.getTitle());
        }
      }
      else {
        title = properties.get(JCR_TITLE, String.class);
      }
    }
    if (!descriptionHidden) {
      if (descriptionFromPage) {
        if (targetPage != null) {
          description = targetPage.getDescription();
        }
      }
      else {
        description = properties.get(JCR_DESCRIPTION, String.class);
      }
    }

    // resolve rich text in description field
    description = richTextHandler.get(description).buildMarkup();

  }

  @Override
  public @NotNull Media getMediaObject() {
    return media;
  }

  @Override
  public @NotNull Link getLinkObject() {
    return link;
  }

  @Override
  public boolean isActionsEnabled() {
    return actionsEnabled;
  }

  @Override
  public List<ListItem> getActions() {
    return Collections.unmodifiableList(actions);
  }

  @Override
  public String getLinkURL() {
    return link.getUrl();
  }

  @Override
  public boolean isImageLinkHidden() {
    return imageLinkHidden;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public boolean isTitleLinkHidden() {
    return titleLinkHidden;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getTitleType() {
    return titleType;
  }

}
