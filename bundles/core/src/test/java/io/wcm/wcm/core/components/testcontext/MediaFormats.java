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
package io.wcm.wcm.core.components.testcontext;

import static io.wcm.handler.media.format.MediaFormatBuilder.create;

import io.wcm.handler.media.format.MediaFormat;

/**
 * Media formats
 */
public final class MediaFormats {

  private MediaFormats() {
    // constants only
  }

  private static final String[] IMAGE_FILE_EXTENSIONS = new String[] { "gif", "jpg", "png" };

  /**
   * Square
   */
  public static final MediaFormat SQUARE = create("square")
      .label("Square")
      .ratio(1, 1)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Landscape
   */
  public static final MediaFormat LANDSCAPE = create("landscape")
      .label("Landscape")
      .ratio(16, 9)
      .extensions(IMAGE_FILE_EXTENSIONS)
      .build();

  /**
   * Download
   */
  public static final MediaFormat DOWNLOAD = create("download")
      .label("Download")
      .extensions("pdf", "zip", "ppt", "pptx", "doc", "docx", "jpg", "tif")
      .download(true)
      .build();

}
