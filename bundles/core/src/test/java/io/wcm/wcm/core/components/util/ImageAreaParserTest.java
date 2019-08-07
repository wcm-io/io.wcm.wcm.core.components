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
package io.wcm.wcm.core.components.util;

import static io.wcm.samples.core.testcontext.AppAemContext.CONTENT_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.google.common.collect.ImmutableList;

import io.wcm.handler.link.LinkHandler;
import io.wcm.samples.core.testcontext.AppAemContext;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.core.components.impl.models.helpers.ImageAreaImpl;

@ExtendWith(AemContextExtension.class)
public class ImageAreaParserTest {

  private static final String VALID_CONTENT_REF = CONTENT_ROOT;
  private static final String INVALID_CONTENT_REF = CONTENT_ROOT + "/invalid";

  public static final String MAP_STRING = "[circle(256,256,256)\"http://myhost\"|\"\"|\"\"|(0.2000,0.3001,0.2000)]"
      + "[rect(256,171,1023,682)\"" + VALID_CONTENT_REF + "\"|\"\"|\"altText\"|(0.1992,0.2005,0.7992,0.7995)]"
      + "[poly(917,344,1280,852,532,852)\"http://myhost\"|\"_blank\"|\"\"|(0.7164,0.4033,1.0000,0.9988,0.4156,0.9988)]"
      // this rect has an invalid content reference and thus should be removed during parsing
      + "[rect(256,171,1023,682)\"" + INVALID_CONTENT_REF + "\"|\"\"|\"altText\"|(0.1992,0.2005,0.7992,0.7995)]";

  public static final List<ImageArea> EXPECTED_AREAS = ImmutableList.of(
      new ImageAreaImpl("circle", "256,256,256", "0.2000,0.3001,0.2000", "http://myhost", "", ""),
      new ImageAreaImpl("rect", "256,171,1023,682", "0.1992,0.2005,0.7992,0.7995", VALID_CONTENT_REF + ".html", "", "altText"),
      new ImageAreaImpl("poly", "917,344,1280,852,532,852", "0.7164,0.4033,1.0000,0.9988,0.4156,0.9988", "http://myhost", "_blank", ""));

  private final AemContext context = AppAemContext.newAemContext();

  private LinkHandler linkHandler;

  @BeforeEach
  void setUp() {
    linkHandler = AdaptTo.notNull(context.request(), LinkHandler.class);
  }

  @Test
  void testValidMap() {
    List<ImageArea> areas = ImageAreaParser.buildFromMapString(MAP_STRING, linkHandler);
    assertEquals(EXPECTED_AREAS, areas);
  }

  @Test
  void testNull() {
    assertNull(ImageAreaParser.buildFromMapString(null, linkHandler));
  }

  @Test
  void testEmptyString() {
    assertNull(ImageAreaParser.buildFromMapString("", linkHandler));
  }

  @Test
  void testInvalidString() {
    assertNull(ImageAreaParser.buildFromMapString("[xyz][", linkHandler));
  }

}
