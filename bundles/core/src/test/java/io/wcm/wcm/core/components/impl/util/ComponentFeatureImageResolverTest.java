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
package io.wcm.wcm.core.components.impl.util;

import static com.adobe.cq.wcm.core.components.models.Image.PN_ALT_VALUE_FROM_PAGE_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_IMAGE_FROM_PAGE_IMAGE;
import static com.adobe.cq.wcm.core.components.models.Page.NN_PAGE_FEATURED_IMAGE;
import static com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
import static com.day.cq.dam.api.DamConstants.DC_TITLE;
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_AUTOCROP;
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_FORMATS;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_ALTTEXT_STANDARD;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_IS_DECORATIVE_STANDARD;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.DAM_ROOT;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.testcontext.AppAemContext;
import io.wcm.wcm.core.components.testcontext.MediaFormats;

@ExtendWith(AemContextExtension.class)
class ComponentFeatureImageResolverTest {

  private static final String COMPONENT_RESOURCE_TYPE = "app1/components/comp1";

  private final AemContext context = AppAemContext.newAemContext();

  private Page page1;
  private Page page2;
  private Asset asset1;
  private Asset asset2;
  private MediaHandler mediaHandler;

  @BeforeEach
  void setUp() {
    context.create().resource("/apps/" + COMPONENT_RESOURCE_TYPE,
        PN_COMPONENT_MEDIA_FORMATS, MediaFormats.LANDSCAPE.getName(),
        PN_COMPONENT_MEDIA_AUTOCROP, true);

    page1 = context.create().page(CONTENT_ROOT + "/page1");
    page2 = context.create().page(CONTENT_ROOT + "/page2");

    asset1 = context.create().asset(DAM_ROOT + "/sample1.jpg", 160, 160, ContentType.JPEG,
        DC_TITLE, "Asset 1 Title",
        DC_DESCRIPTION, "Asset 1 Description");
    context.create().assetRenditionWebEnabled(asset1);
    asset2 = context.create().asset(DAM_ROOT + "/sample2.jpg", 160, 160, ContentType.JPEG,
        DC_TITLE, "Asset 2 Title",
        DC_DESCRIPTION, "Asset 2 Description");
    context.create().assetRenditionWebEnabled(asset2);

    mediaHandler = AdaptTo.notNull(page1.getContentResource(), MediaHandler.class);
  }

  @Test
  void testInvalidImage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, "/content/dam/invalid.jpg");

    Media media = newComponentFeatureImageResolver(component)
        .buildMedia();

    assertFalse(media.isValid());
  }

  @Test
  @SuppressWarnings("null")
  void testComponentImage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt");

    Media media = newComponentFeatureImageResolver(component)
        .buildMedia();

    assertTrue(media.isValid());
    assertEquals("/content/dam/sample/sample1.jpg/_jcr_content/renditions/original.image_file.160.90.0,35,160,125.file/sample1.jpg", media.getUrl());
    assertEquals("My Alt", media.getAsset().getAltText());
  }

  @Test
  @SuppressWarnings("null")
  void testComponentImage_Decorative() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_MEDIA_IS_DECORATIVE_STANDARD, true);

    Media media = newComponentFeatureImageResolver(component)
        .buildMedia();

    assertTrue(media.isValid());
    assertEquals("/content/dam/sample/sample1.jpg/_jcr_content/renditions/original.image_file.160.90.0,35,160,125.file/sample1.jpg", media.getUrl());
    assertEquals("", media.getAsset().getAltText());
  }

  @Test
  @SuppressWarnings("null")
  void testComponentImage_Decorative_Policy() {
    context.contentPolicyMapping(COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_IS_DECORATIVE_STANDARD, true);

    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt");

    Media media = newComponentFeatureImageResolver(component)
        .buildMedia();

    assertTrue(media.isValid());
    assertEquals("/content/dam/sample/sample1.jpg/_jcr_content/renditions/original.image_file.160.90.0,35,160,125.file/sample1.jpg", media.getUrl());
    assertEquals("", media.getAsset().getAltText());
  }

  @Test
  void testComponentImageFromPage_NoTargetPage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, true);

    Media media = newComponentFeatureImageResolver(component)
        .buildMedia();

    assertFalse(media.isValid());
  }

  @Test
  void testComponentImageFromPage_NoFeatureImage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, true);

    Media media = newComponentFeatureImageResolver(component)
        .targetPage(page2)
        .buildMedia();

    assertFalse(media.isValid());
  }

  @Test
  @SuppressWarnings("null")
  void testComponentImageFromPage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, true,
        PN_ALT_VALUE_FROM_PAGE_IMAGE, true);

    // create feature image in page2
    context.create().resource(page2, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset2.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "Feature Alt");

    Media media = newComponentFeatureImageResolver(component)
        .targetPage(page2)
        .buildMedia();

    assertTrue(media.isValid());
    assertEquals("/content/dam/sample/sample2.jpg/_jcr_content/renditions/original.image_file.160.90.0,35,160,125.file/sample2.jpg", media.getUrl());
    assertEquals("Feature Alt", media.getAsset().getAltText());
  }

  @Test
  void testComponentImageFromPage_Disabled() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, false,
        PN_ALT_VALUE_FROM_PAGE_IMAGE, true);

    // create feature image in page2
    context.create().resource(page2, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset2.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "Feature Alt");

    Media media = newComponentFeatureImageResolver(component)
        .targetPage(page2)
        .buildMedia();

    assertFalse(media.isValid());
  }

  @Test
  @SuppressWarnings("null")
  void testComponentImageFromPage_FallbackCurrentPage() {
    Resource component = context.create().resource(page1, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, true,
        PN_ALT_VALUE_FROM_PAGE_IMAGE, true);

    // create feature image in page1
    context.create().resource(page1, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset2.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "Current Page Alt");

    Media media = newComponentFeatureImageResolver(component)
        .targetPage(page2)
        .buildMedia();

    assertTrue(media.isValid());
    assertEquals("/content/dam/sample/sample2.jpg/_jcr_content/renditions/original.image_file.160.90.0,35,160,125.file/sample2.jpg", media.getUrl());
    assertEquals("Current Page Alt", media.getAsset().getAltText());
  }

  @Test
  void testComponentImageFromPage_NoFallbackToCurrentPageIfComponentFromDifferentPage() {
    // create component in page2 (current page is page1)
    Resource component = context.create().resource(page2, "comp1",
        PROPERTY_RESOURCE_TYPE, COMPONENT_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset1.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "My Alt",
        PN_IMAGE_FROM_PAGE_IMAGE, true,
        PN_ALT_VALUE_FROM_PAGE_IMAGE, true);

    // create feature image in page1
    context.create().resource(page1, NN_PAGE_FEATURED_IMAGE,
        PN_MEDIA_REF_STANDARD, asset2.getPath(),
        PN_MEDIA_ALTTEXT_STANDARD, "Current Page Alt");

    Media media = newComponentFeatureImageResolver(component)
        .targetPage(page2)
        .buildMedia();

    assertFalse(media.isValid());
  }

  @SuppressWarnings("deprecation")
  private ComponentFeatureImageResolver newComponentFeatureImageResolver(@NotNull Resource resource) {
    context.currentResource(resource);

    SlingBindings slingBindings = (SlingBindings)context.request().getAttribute(SlingBindings.class.getName());
    Style currentStyle = (Style)slingBindings.get(WCMBindings.CURRENT_STYLE);

    return new ComponentFeatureImageResolver(resource, page1, currentStyle, mediaHandler);
  }

}
