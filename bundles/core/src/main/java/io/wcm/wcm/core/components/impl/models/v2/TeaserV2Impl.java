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
package io.wcm.wcm.core.components.impl.models.v2;

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
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.richtext.RichTextHandler;
import io.wcm.handler.richtext.TextMode;
import io.wcm.sling.models.annotations.AemObject;
import io.wcm.wcm.core.components.commons.link.LinkWrapper;
import io.wcm.wcm.core.components.impl.models.helpers.AbstractComponentImpl;
import io.wcm.wcm.core.components.impl.models.helpers.LinkListItemV2Impl;
import io.wcm.wcm.core.components.impl.util.ComponentFeatureImageResolver;
import io.wcm.wcm.core.components.impl.util.HandlerUnwrapper;
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
    resourceType = TeaserV2Impl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserV2Impl extends AbstractComponentImpl implements Teaser, MediaMixin {

  static final String RESOURCE_TYPE = "wcm-io/wcm/core/components/teaser/v2/teaser";

  private static final String CTA_ID_PREFIX = "cta";

  @AemObject
  private Style currentStyle;
  @Self
  private MediaHandler mediaHandler;
  @Self
  private LinkHandler linkHandler;
  @Self
  private RichTextHandler richTextHandler;

  private Media media;
  protected LinkWrapper link;
  private final List<ListItem> actions = new ArrayList<>();
  private Page targetPage;

  private String pretitle;
  private String title;
  private String description;
  private String titleType;
  private boolean showTitleType;
  private boolean actionsEnabled;
  private boolean imageLinkHidden;
  private boolean titleLinkHidden;

  @PostConstruct
  @SuppressWarnings({ "java:S3776", "java:S6541" }) // ignore complexity
  private void activate() {
    ValueMap properties = resource.getValueMap();

    // read style properties
    boolean pretitleHidden = currentStyle.get(PN_PRETITLE_HIDDEN, false);
    boolean titleHidden = currentStyle.get(PN_TITLE_HIDDEN, false);
    boolean descriptionHidden = currentStyle.get(PN_DESCRIPTION_HIDDEN, false);
    titleType = currentStyle.get(PN_TITLE_TYPE, (String)null);
    showTitleType = currentStyle.get(Teaser.PN_SHOW_TITLE_TYPE, false);
    imageLinkHidden = currentStyle.get(PN_IMAGE_LINK_HIDDEN, false);
    titleLinkHidden = currentStyle.get(PN_TITLE_LINK_HIDDEN, false);
    boolean actionsDisabled = currentStyle.get(PN_ACTIONS_DISABLED, false);

    // read component properties
    actionsEnabled = properties.get(PN_ACTIONS_ENABLED, getActionsEnabledDefault()) && !actionsDisabled;
    boolean titleFromPage = properties.get(PN_TITLE_FROM_PAGE, false);
    boolean descriptionFromPage = properties.get(PN_DESCRIPTION_FROM_PAGE, false);

    // resolve actions with links
    if (actionsEnabled) {
      Resource actionsNode = resource.getChild(NN_ACTIONS);
      if (actionsNode != null) {
        for (Resource actionResource : actionsNode.getChildren()) {
          String actionTitle = actionResource.getValueMap().get(PN_ACTION_TEXT, String.class);
          LinkWrapper actionLink = new LinkWrapper(linkHandler.get(actionResource).build());
          if (actionTitle != null && actionLink.isValid()) {
            actions.add(newLinkListItem(actionTitle, actionLink, CTA_ID_PREFIX));
            if (targetPage == null) {
              // get target page from first action
              targetPage = actionLink.getLinkObject().getTargetPage();
            }
          }
        }
      }
    }

    // if actions are enabled and present, primary link is not enabled
    if (actionsEnabled && !this.actions.isEmpty()) {
      link = new LinkWrapper(linkHandler.invalid());
    }
    // otherwise resolve primary teaser link
    else {
      link = new LinkWrapper(HandlerUnwrapper.get(linkHandler, resource).build());
      targetPage = link.getLinkObject().getTargetPage();
    }

    // resolve teaser image and alt. text
    media = new ComponentFeatureImageResolver(resource, getCurrentPage(), currentStyle, mediaHandler)
        .targetPage(targetPage)
        .mediaHandlerProperty(PROP_CSS_CLASS, "cmp-image__image")
        .buildMedia();

    // read title and description
    if (!pretitleHidden) {
      pretitle = properties.get("pretitle", String.class);
    }
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
          // page description is by default no rich text
          description = richTextHandler.get(description).textMode(TextMode.PLAIN).buildMarkup();
        }
      }
      else {
        description = properties.get(JCR_DESCRIPTION, String.class);
        // description in teaser is rich text
        description = richTextHandler.get(description).textMode(TextMode.XHTML).buildMarkup();
      }
    }

  }

  protected boolean getActionsEnabledDefault() {
    return true;
  }

  @Override
  public @NotNull Media getMediaObject() {
    return media;
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
  public Link getLink() {
    return link.orNull();
  }

  /**
   * @deprecated Deprecated in API
   */
  @Override
  @Deprecated(forRemoval = true)
  @JsonIgnore
  public String getLinkURL() {
    return link.getURL();
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
  public String getPretitle() {
    return pretitle;
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
    if (showTitleType) {
      titleType = resource.getValueMap().get(Teaser.PN_TITLE_TYPE, titleType);
    }
    return titleType;
  }


  // --- data layer ---

  @Override
  protected @NotNull ComponentData getComponentData() {
    return DataLayerBuilder.extending(super.getComponentData()).asComponent()
        .withTitle(this::getTitle)
        .withLinkUrl(link::getURL)
        .withDescription(this::getDescription)
        .build();
  }

  protected ListItem newLinkListItem(@NotNull String newTitle, @NotNull LinkWrapper newLink, @NotNull String itemIdPrefix) {
    return new LinkListItemV2Impl(newTitle, newLink, itemIdPrefix,
        getId(), getParentComponent(), this.resource);
  }

}
