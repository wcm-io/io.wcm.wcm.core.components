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
package io.wcm.wcm.core.components.impl.models.wcmio.v1;

import static com.adobe.cq.wcm.core.components.models.Image.PN_ALT_VALUE_FROM_DAM;
import static com.adobe.cq.wcm.core.components.models.Image.PN_DISPLAY_POPUP_TITLE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_IS_DECORATIVE;
import static com.adobe.cq.wcm.core.components.models.Image.PN_MAP;
import static com.adobe.cq.wcm.core.components.models.Image.PN_TITLE_VALUE_FROM_DAM;
import static com.adobe.cq.wcm.core.components.models.Image.PN_UUID_DISABLED;
import static com.day.cq.commons.ImageResource.PN_ALT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_UUID;
import static com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
import static com.day.cq.dam.api.DamConstants.DC_TITLE;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_EXTERNAL_REF;
import static io.wcm.handler.link.LinkNameConstants.PN_LINK_TITLE;
import static io.wcm.handler.media.MediaNameConstants.NN_MEDIA_INLINE_STANDARD;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertInvalidLink;
import static io.wcm.samples.core.testcontext.TestUtils.assertInvalidMedia;
import static io.wcm.samples.core.testcontext.TestUtils.assertValidLink;
import static io.wcm.samples.core.testcontext.TestUtils.assertValidMedia;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.wcmio.v1.ResponsiveImageImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.samples.core.testcontext.ImageAreaTestData;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.models.ResponsiveImage;

@ExtendWith(AemContextExtension.class)
class ResponsiveImageImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private Asset asset;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE);

    page = context.create().page(CONTENT_ROOT + "/page1");
    asset = context.create().asset(DAM_ROOT + "/sample.jpg", 160, 90, ContentType.JPEG,
        DC_TITLE, "Asset Title",
        DC_DESCRIPTION, "Asset Description");
  }

  @Test
  void testNoImage() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertNull(underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getUuid());
    assertTrue(underTest.displayPopupTitle());
    assertNull(underTest.getFileReference());
    assertNull(underTest.getAreas());

    assertInvalidMedia(underTest);
    assertInvalidLink(underTest);

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

  @Test
  void testInvalidAssetReference() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, "/content/dam/invalid"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertInvalidMedia(underTest);
    assertInvalidLink(underTest);
  }

  @Test
  void testWithAssetImage() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "Resource Title",
        PN_ALT, "Resource Alt"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    String expectedMediaUrl = DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original./sample.jpg";

    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("Asset Description", underTest.getAlt());
    assertEquals("", underTest.getUuid());
    assertTrue(underTest.displayPopupTitle());
    assertEquals(asset.getPath(), underTest.getFileReference());
    assertNull(underTest.getAreas());

    assertValidMedia(underTest, expectedMediaUrl);
    assertInvalidLink(underTest);
  }

  @Test
  void testWithUploadedImage() {
    Resource imageResource = context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        NN_MEDIA_INLINE_STANDARD + "Name", "file1.png");
    context.load().binaryFile("/files/test.png", imageResource.getPath() + "/" + NN_MEDIA_INLINE_STANDARD, ContentType.PNG);
    context.currentResource(imageResource);

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    String expectedMediaUrl = "/content/sample/en/page1/_jcr_content/image/file./file1.png";

    assertNull(underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getUuid());
    assertTrue(underTest.displayPopupTitle());
    assertNull(underTest.getFileReference());
    assertNull(underTest.getAreas());

    assertValidMedia(underTest, expectedMediaUrl);
    assertInvalidLink(underTest);
  }

  @Test
  void testWithImageAndLink() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("Asset Description", underTest.getAlt());

    assertValidLink(underTest, "http://myhost");
  }

  @Test
  void testWithImageAndLink_Decorative() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost",
        PN_IS_DECORATIVE, true));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertNull(underTest.getAlt());

    assertInvalidLink(underTest);
  }

  @Test
  void testWithImageAndLink_Decorative_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_IS_DECORATIVE, true);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertNull(underTest.getAlt());

    assertInvalidLink(underTest);
  }

  @Test
  void testWithImage_TitleAltNotFormAsset() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "Resource Title",
        PN_ALT, "Resource Alt",
        PN_TITLE_VALUE_FROM_DAM, false,
        PN_ALT_VALUE_FROM_DAM, false));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("Resource Title", underTest.getTitle());
    assertEquals("Resource Alt", underTest.getAlt());
  }

  @Test
  void testWithImage_TitleAltNotFormAsset_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_TITLE_VALUE_FROM_DAM, false,
        PN_ALT_VALUE_FROM_DAM, false);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "Resource Title",
        PN_ALT, "Resource Alt"));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("Resource Title", underTest.getTitle());
    assertEquals("Resource Alt", underTest.getAlt());
  }

  @Test
  void testWithNoImageAsset() {
    Asset pdfAsset = context.create().asset(DAM_ROOT + "/file1.pdf", "/files/test.pdf", ContentType.PDF);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, pdfAsset.getPath()));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertInvalidMedia(underTest);
  }

  @Test
  void testDisplayPopupTitle() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_DISPLAY_POPUP_TITLE, false));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertFalse(underTest.displayPopupTitle());
  }

  @Test
  void testDisplayPopupTitle_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_DISPLAY_POPUP_TITLE, false);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertFalse(underTest.displayPopupTitle());
  }

  @Test
  void testUUID() {
    Resource resource = AdaptTo.notNull(asset, Resource.class);
    ModifiableValueMap props = AdaptTo.notNull(resource, ModifiableValueMap.class);
    props.put(JCR_UUID, "test-uuid");

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals("test-uuid", underTest.getUuid());
  }

  @Test
  void testUUID_Disabled() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_UUID_DISABLED, true);

    Resource resource = AdaptTo.notNull(asset, Resource.class);
    ModifiableValueMap props = AdaptTo.notNull(resource, ModifiableValueMap.class);
    props.put(JCR_UUID, "test-uuid");

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertNull(underTest.getUuid());
  }

  @Test
  void testAreas() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_MAP, ImageAreaTestData.MAP_STRING));

    ResponsiveImage underTest = AdaptTo.notNull(context.request(), ResponsiveImage.class);

    assertEquals(ImageAreaTestData.EXPECTED_AREAS, underTest.getAreas());
  }

}