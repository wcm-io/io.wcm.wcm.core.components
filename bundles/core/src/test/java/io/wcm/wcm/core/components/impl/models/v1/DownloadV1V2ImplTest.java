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

import static com.adobe.cq.wcm.core.components.models.Download.PN_ACTION_TEXT;
import static com.adobe.cq.wcm.core.components.models.Download.PN_DESCRIPTION_FROM_ASSET;
import static com.adobe.cq.wcm.core.components.models.Download.PN_DISPLAY_FILENAME;
import static com.adobe.cq.wcm.core.components.models.Download.PN_DISPLAY_FORMAT;
import static com.adobe.cq.wcm.core.components.models.Download.PN_DISPLAY_SIZE;
import static com.adobe.cq.wcm.core.components.models.Download.PN_HIDE_TITLE_LINK;
import static com.adobe.cq.wcm.core.components.models.Download.PN_INLINE;
import static com.adobe.cq.wcm.core.components.models.Download.PN_TITLE_FROM_ASSET;
import static com.adobe.cq.wcm.core.components.models.Download.PN_TITLE_TYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_DESCRIPTION;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.dam.api.DamConstants.DC_DESCRIPTION;
import static com.day.cq.dam.api.DamConstants.DC_TITLE;
import static io.wcm.handler.media.MediaNameConstants.NN_MEDIA_INLINE_STANDARD;
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static io.wcm.wcm.core.components.impl.models.v1.DownloadV1V2Impl.RESOURCE_TYPE_V1;
import static io.wcm.wcm.core.components.impl.models.v1.DownloadV1V2Impl.RESOURCE_TYPE_V2;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.CONTENT_ROOT;
import static io.wcm.wcm.core.components.testcontext.AppAemContext.DAM_ROOT;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertInvalidMedia;
import static io.wcm.wcm.core.components.testcontext.TestUtils.assertValidMedia;
import static io.wcm.wcm.core.components.testcontext.TestUtils.loadComponentDefinition;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.adobe.cq.wcm.core.components.models.Download;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.core.components.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class DownloadV1V2ImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;

  @BeforeEach
  void setUp() {
    loadComponentDefinition(context, RESOURCE_TYPE_V1);

    page = context.create().page(CONTENT_ROOT + "/page1");
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testEmpty(String resourceType) {
    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    assertNull(underTest.getTitle());
    assertNull(underTest.getDescription());
    assertNull(underTest.getUrl());
    assertNull(underTest.getActionText());
    assertNull(underTest.getTitleType());
    assertNull(underTest.getSize());
    assertNull(underTest.getExtension());
    assertTrue(underTest.displaySize());
    assertNull(underTest.getFormat());
    assertTrue(underTest.displayFormat());
    assertNull(underTest.getFilename());
    assertTrue(underTest.displayFilename());
    assertFalse(underTest.hideTitleLink());
    assertNotNull(underTest.getId());

    assertInvalidMedia(underTest);
    assertNull(underTest.getData());

    assertEquals(resourceType, underTest.getExportedType());
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testEmpty_ContentPolicy(String resourceType) {
    context.contentPolicyMapping(resourceType,
        PN_TITLE_TYPE, "h3",
        PN_DISPLAY_SIZE, false,
        PN_DISPLAY_FORMAT, false,
        PN_DISPLAY_FILENAME, false,
        PN_HIDE_TITLE_LINK, true);

    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    assertEquals("h3", underTest.getTitleType());
    assertFalse(underTest.displaySize());
    assertFalse(underTest.displayFormat());
    assertFalse(underTest.displayFilename());
    assertTrue(underTest.hideTitleLink());
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testAssetReference(String resourceType) {
    Asset asset = context.create().asset(DAM_ROOT + "/file1.pdf", "/files/test.pdf", ContentType.PDF);

    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "My Title",
        JCR_DESCRIPTION, "My Description",
        PN_ACTION_TEXT, "My Action"));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    String expectedMediaUrl = "/content/dam/sample/file1.pdf/_jcr_content/renditions/original.media_file.download_attachment.file/file1.pdf";

    assertEquals("My Title", underTest.getTitle());
    assertEquals("My Description", underTest.getDescription());
    assertEquals(expectedMediaUrl, underTest.getUrl());
    assertEquals("My Action", underTest.getActionText());
    assertNull(underTest.getTitleType());
    assertEquals("15 KB", underTest.getSize());
    assertEquals("pdf", underTest.getExtension());
    assertTrue(underTest.displaySize());
    assertEquals(ContentType.PDF, underTest.getFormat());
    assertTrue(underTest.displayFormat());
    assertEquals("file1.pdf", underTest.getFilename());
    assertTrue(underTest.displayFilename());

    assertValidMedia(underTest, expectedMediaUrl);
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testAssetReference_Inline(String resourceType) {
    Asset asset = context.create().asset(DAM_ROOT + "/file1.pdf", "/files/test.pdf", ContentType.PDF);

    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_INLINE, true));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    String expectedMediaUrl = "/content/dam/sample/file1.pdf/_jcr_content/renditions/original./file1.pdf";

    assertEquals(expectedMediaUrl, underTest.getUrl());
    assertEquals("15 KB", underTest.getSize());

    assertValidMedia(underTest, expectedMediaUrl);
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testAssetReference_Inline_0size(String resourceType) {
    Asset asset = context.create().asset(DAM_ROOT + "/file1.pdf", new ByteArrayInputStream(new byte[0]), ContentType.PDF);

    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        PN_INLINE, true));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    String expectedMediaUrl = "/content/dam/sample/file1.pdf/_jcr_content/renditions/original./file1.pdf";

    assertEquals(expectedMediaUrl, underTest.getUrl());
    assertNull(underTest.getSize());

    assertValidMedia(underTest, expectedMediaUrl);
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testAssetReference_TitleDescFromAsset(String resourceType) {
    Asset asset = context.create().asset(DAM_ROOT + "/file1.pdf", "/files/test.pdf", ContentType.PDF,
        DC_TITLE, "My Asset Title",
        DC_DESCRIPTION, "My Asset Description");

    context.currentResource(context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType,
        PN_MEDIA_REF_STANDARD, asset.getPath(),
        JCR_TITLE, "My Title",
        JCR_DESCRIPTION, "My Description",
        PN_TITLE_FROM_ASSET, true,
        PN_DESCRIPTION_FROM_ASSET, true));
    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    assertEquals("My Asset Title", underTest.getTitle());
    assertEquals("My Asset Description", underTest.getDescription());
  }

  @ParameterizedTest
  @ValueSource(strings = { RESOURCE_TYPE_V1, RESOURCE_TYPE_V2 })
  void testUploadedFile(String resourceType) {
    Resource downloadResource = context.create().resource(page, "download",
        PROPERTY_RESOURCE_TYPE, resourceType,
        NN_MEDIA_INLINE_STANDARD + "Name", "file1.pdf",
        JCR_TITLE, "My Title",
        JCR_DESCRIPTION, "My Description",
        PN_ACTION_TEXT, "My Action");
    context.load().binaryFile("/files/test.pdf", downloadResource.getPath() + "/" + NN_MEDIA_INLINE_STANDARD, ContentType.PDF);
    context.currentResource(downloadResource);

    Download underTest = AdaptTo.notNull(context.request(), Download.class);

    String expectedMediaUrl = "/content/sample/en/page1/_jcr_content/download/file.media_file.download_attachment.file/file1.pdf";

    assertEquals("My Title", underTest.getTitle());
    assertEquals("My Description", underTest.getDescription());
    assertEquals(expectedMediaUrl, underTest.getUrl());
    assertEquals("My Action", underTest.getActionText());
    assertNull(underTest.getTitleType());
    assertEquals("15 KB", underTest.getSize());
    assertEquals("pdf", underTest.getExtension());
    assertTrue(underTest.displaySize());
    assertEquals(ContentType.PDF, underTest.getFormat());
    assertTrue(underTest.displayFormat());
    assertEquals("file1.pdf", underTest.getFilename());
    assertTrue(underTest.displayFilename());

    assertValidMedia(underTest, expectedMediaUrl);

    assertEquals(resourceType, underTest.getExportedType());
  }

}
