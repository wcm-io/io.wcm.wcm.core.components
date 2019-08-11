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
package io.wcm.wcm.core.components.examples.config;

import static io.wcm.handler.media.format.MediaFormatBuilder.create;

import io.wcm.handler.media.format.MediaFormat;

/**
 * Media formats
 */
public final class MediaFormats {

  private MediaFormats() {
    // constants only
  }

  private static final String[] IMAGE_FILE_EXTENSIONS = new String[] {
      "gif", "jpg", "png", "tif", "webp", "svg" };

  /**
   * Square
   */
  public static final MediaFormat SQUARE = create("square")
      .label("Square")
      .ratio(1, 1)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Wide
   */
  public static final MediaFormat WIDE = create("wide")
      .label("Wide")
      .ratio(16, 9)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Extra Wide
   */
  public static final MediaFormat EXTRA_WIDE = create("extra_wide")
      .label("Extra Wide")
      .ratio(32, 10)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Portrait
   */
  public static final MediaFormat PORTRAIT = create("portrait")
      .label("Portrait")
      .ratio(1, 2)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Fixed size
   */
  public static final MediaFormat FIXED_SIZE = create("fixed_size")
      .label("Fixed Size")
      .fixedDimension(500, 375)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Download
   */
  public static final MediaFormat DOWNLOAD = create("download")
      .label("Download")
      .extensions("pdf", "zip", "jpg", "tif")
      .download(true)
      .build();

}
