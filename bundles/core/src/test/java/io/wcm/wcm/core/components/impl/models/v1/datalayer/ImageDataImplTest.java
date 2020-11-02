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

import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.Page;

import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.impl.models.helpers.IdGenerationTest;

@ExtendWith(AemContextExtension.class)
class ImageDataImplTest {

  private final AemContext context = AppAemContext.newAemContext();

  private DummyComponent component;

  @BeforeEach
  void setUp() {
    context.addModelsForClasses(DummyComponent.class);

    Asset asset = context.create().asset("/content/dam/my-asset.png", 10, 10, "image/png",
        TagConstants.PN_TAGS, "tag1");

    Page page = context.create().page(IdGenerationTest.PAGE_PATH);
    Resource resource = context.create().resource(page, IdGenerationTest.RESOURCE_NAME,
        PROPERTY_RESOURCE_TYPE, IdGenerationTest.RESOURCE_TYPE,
        PN_MEDIA_REF_STANDARD, asset.getPath());
    context.currentResource(resource);

    component = AdaptTo.notNull(context.request(), DummyComponent.class);
  }

  @Test
  void testProperties() {
    ImageData underTest = new ImageDataImpl(component, context.currentResource());

    assertEquals(IdGenerationTest.EXPECTED_ID, underTest.getId());
    assertNull(underTest.getParentId());
    assertEquals(IdGenerationTest.RESOURCE_TYPE, underTest.getType());

    AssetData assetData = underTest.getAssetData();
    assertNotNull(assetData);

    assertNotNull(assetData.getId());
    assertNull(assetData.getLastModifiedDate());
    assertEquals("image/png", assetData.getFormat());
    assertEquals("/content/dam/my-asset.png", assetData.getUrl());
    assertArrayEquals(new String[] { "tag1" }, assetData.getTags());
  }

  @Test
  void testJson() throws JSONException {
    ImageData underTest = new ImageDataImpl(component, context.currentResource());
    JSONAssert.assertEquals("{'" + IdGenerationTest.EXPECTED_ID + "': {"
        + "'@type': '" + IdGenerationTest.RESOURCE_TYPE + "', "
        + "'image': {"
        + "'@type': 'image/png', "
        + "'repo:path': '/content/dam/my-asset.png', "
        + "'repo:id': '', "
        + "'xdm:tags': ['tag1']"
        + "}}}", underTest.getJson(), true);
  }

}
