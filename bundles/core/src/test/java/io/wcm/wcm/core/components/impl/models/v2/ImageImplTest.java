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

import static com.adobe.cq.wcm.core.components.models.Image.PN_ALT_VALUE_FROM_DAM;
import static com.adobe.cq.wcm.core.components.models.Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS;
import static com.adobe.cq.wcm.core.components.models.Image.PN_DESIGN_LAZY_LOADING_ENABLED;
import static com.adobe.cq.wcm.core.components.models.Image.PN_DESIGN_LAZY_THRESHOLD;
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
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_AUTOCROP;
import static io.wcm.handler.media.MediaNameConstants.PN_COMPONENT_MEDIA_FORMATS;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.samples.core.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.samples.core.testcontext.TestUtils.assertInvalidLink;
import static io.wcm.samples.core.testcontext.TestUtils.assertInvalidMedia;
import static io.wcm.samples.core.testcontext.TestUtils.assertValidLink;
import static io.wcm.samples.core.testcontext.TestUtils.assertValidMedia;
import static io.wcm.samples.core.testcontext.TestUtils.loadComponentDefinition;
import static io.wcm.wcm.core.components.impl.models.helpers.DataLayerTestUtils.enableDataLayer;
import static io.wcm.wcm.core.components.impl.models.v2.ImageImpl.RESOURCE_TYPE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.samples.core.testcontext.ImageAreaTestData;
import io.wcm.samples.core.testcontext.MediaFormats;
import io.wcm.samples.core.testcontext.ResourceTypeForcingResourceWrapper;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;

@ExtendWith(AemContextExtension.class)
class ImageImplTest {

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
    // create web rendition to test auto-cropping
    context.create().assetRendition(asset, "cq5dam.web.160.90.jpg", 160, 90, ContentType.JPEG);
  }

  @Test
  @SuppressWarnings("deprecation")
  void testNoImage() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertNull(underTest.getSrc());
    assertNull(underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getUuid());
    assertNull(underTest.getLink());
    assertTrue(underTest.displayPopupTitle());
    assertNull(underTest.getFileReference());
    assertNull(underTest.getJson());
    assertArrayEquals(new int[0], underTest.getWidths());
    assertNull(underTest.getSrcUriTemplate());
    assertFalse(underTest.isLazyEnabled());
    assertEquals(0, underTest.getLazyThreshold());
    assertNull(underTest.getAreas());
    assertFalse(underTest.isDecorative());
    assertNotNull(underTest.getId());

    assertInvalidMedia(underTest);
    assertInvalidLink(underTest);
    assertNull(underTest.getData());

    assertEquals(RESOURCE_TYPE, underTest.getExportedType());
  }

  @Test
  void testInvalidAssetReference() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, "/content/dam/invalid"));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

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

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    String expectedMediaUrl = DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original./sample.jpg";

    assertEquals(expectedMediaUrl, underTest.getSrc());
    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("Asset Description", underTest.getAlt());
    assertEquals("", underTest.getUuid());
    assertNull(underTest.getLink());
    assertTrue(underTest.displayPopupTitle());
    assertEquals(asset.getPath(), underTest.getFileReference());
    assertArrayEquals(new int[0], underTest.getWidths());
    assertEquals(page.getPath() + "/_jcr_content/image.imgwidth{.width}.suffix.jpg/sample.jpg", underTest.getSrcUriTemplate());
    assertFalse(underTest.isLazyEnabled());
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

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    String expectedMediaUrl = "/content/sample/en/page1/_jcr_content/image/file./file1.png";

    assertEquals(expectedMediaUrl, underTest.getSrc());
    assertNull(underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getUuid());
    assertNull(underTest.getLink());
    assertTrue(underTest.displayPopupTitle());
    assertNull(underTest.getFileReference());
    assertArrayEquals(new int[0], underTest.getWidths());
    assertEquals(page.getPath() + "/_jcr_content/image.imgwidth{.width}.suffix.png/file1.png", underTest.getSrcUriTemplate());
    assertFalse(underTest.isLazyEnabled());
    assertNull(underTest.getAreas());

    assertValidMedia(underTest, expectedMediaUrl);
    assertInvalidLink(underTest);
  }

  @Test
  @SuppressWarnings("null")
  void testWithImageAndLink() {
    enableDataLayer(context, true);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost"));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("Asset Description", underTest.getAlt());
    assertEquals("http://myhost", underTest.getLink());

    assertValidLink(underTest, "http://myhost");

    ComponentData data = underTest.getData();
    assertNotNull(data);
    assertEquals(RESOURCE_TYPE, data.getType());
    assertEquals("Asset Title", data.getTitle());
    assertEquals("http://myhost", data.getLinkUrl());

    AssetData assetData = ((ImageData)data).getAssetData();
    assertEquals(asset.getPath(), assetData.getUrl());
    assertEquals(asset.getMimeType(), assetData.getFormat());
  }

  @Test
  void testWithImageAndLink_Decorative() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost",
        PN_IS_DECORATIVE, true));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getLink());
    assertTrue(underTest.isDecorative());

    assertInvalidLink(underTest);
  }

  @Test
  void testWithImageAndLink_Decorative_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_IS_DECORATIVE, true,
        PN_DESIGN_LAZY_THRESHOLD, 10);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_LINK_TITLE, ExternalLinkType.ID,
        PN_LINK_EXTERNAL_REF, "http://myhost"));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertNull(underTest.getAlt());
    assertNull(underTest.getLink());
    assertEquals(10, underTest.getLazyThreshold());

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

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

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

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Resource Title", underTest.getTitle());
    assertEquals("Resource Alt", underTest.getAlt());
  }

  @Test
  void testWithNoImageAsset() {
    Asset pdfAsset = context.create().asset(DAM_ROOT + "/file1.pdf", "/files/test.pdf", ContentType.PDF);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, pdfAsset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertInvalidMedia(underTest);
  }

  @Test
  void testDisplayPopupTitle() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_DISPLAY_POPUP_TITLE, false));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertFalse(underTest.displayPopupTitle());
  }

  @Test
  void testDisplayPopupTitle_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_DISPLAY_POPUP_TITLE, false);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertFalse(underTest.displayPopupTitle());
  }

  @Test
  void testLazyEnabled_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_DESIGN_LAZY_LOADING_ENABLED, false); // property is internally named "disabled", value is inverted

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertTrue(underTest.isLazyEnabled());
  }

  @Test
  void testUUID() {
    Resource resource = AdaptTo.notNull(asset, Resource.class);
    ModifiableValueMap props = AdaptTo.notNull(resource, ModifiableValueMap.class);
    props.put(JCR_UUID, "test-uuid");

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

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

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertNull(underTest.getUuid());
  }

  @Test
  void testWidths() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[] { "100", "50", "200", "-123", "0", "junk" });

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("/content/sample/en/page1/_jcr_content/image.imgwidth.100.suffix.jpg/sample.jpg", underTest.getSrc());
    assertEquals(asset.getPath(), underTest.getFileReference());
    assertArrayEquals(new int[] { 50, 100 }, underTest.getWidths());
    assertEquals(page.getPath() + "/_jcr_content/image.imgwidth{.width}.suffix.jpg/sample.jpg", underTest.getSrcUriTemplate());

    assertValidMedia(underTest, DAM_ROOT + "/sample.jpg/_jcr_content/renditions/original./sample.jpg");
  }

  @Test
  void testAreas() {
    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_MAP, ImageAreaTestData.MAP_STRING));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals(ImageAreaTestData.EXPECTED_AREAS, underTest.getAreas());
  }

  @Test
  void testWithImageAutoCropping_ContentPolicy() {
    context.contentPolicyMapping(RESOURCE_TYPE,
        PN_COMPONENT_MEDIA_FORMATS, new String[] { MediaFormats.SQUARE.getName() },
        PN_COMPONENT_MEDIA_AUTOCROP, true);

    context.currentResource(context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath()));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("/content/dam/sample/sample.jpg/_jcr_content/renditions/original.image_file.90.90.35,0,125,90.file/sample.jpg", underTest.getSrc());
  }

  @Test
  void testWithImageAutoCropping_ContentPolicy_WrappedResource() {
    // prepare dummy component that delegates to the component under test
    final String DELEGATE_RESOURCE_TYPE = "myapp/components/delegate";
    context.create().resource("/apps/" + DELEGATE_RESOURCE_TYPE,
        "sling:resourceSuperType", RESOURCE_TYPE);

    context.contentPolicyMapping(DELEGATE_RESOURCE_TYPE,
        PN_COMPONENT_MEDIA_FORMATS, new String[] { MediaFormats.SQUARE.getName() },
        PN_COMPONENT_MEDIA_AUTOCROP, true);

    Resource resource = context.create().resource(page.getContentResource().getPath() + "/image",
        PROPERTY_RESOURCE_TYPE, DELEGATE_RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath());

    // set context resource to wrapped resource
    context.currentResource(new ResourceTypeForcingResourceWrapper(resource, RESOURCE_TYPE));

    Image underTest = AdaptTo.notNull(context.request(), Image.class);

    assertEquals("Asset Title", underTest.getTitle());
    assertEquals("/content/dam/sample/sample.jpg/_jcr_content/renditions/original.image_file.90.90.35,0,125,90.file/sample.jpg", underTest.getSrc());
  }

}
