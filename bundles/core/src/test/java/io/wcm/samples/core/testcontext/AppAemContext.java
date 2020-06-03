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
package io.wcm.samples.core.testcontext;

import static com.day.cq.commons.jcr.JcrConstants.JCR_LANGUAGE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
import static io.wcm.testing.mock.wcmio.handler.ContextPlugins.WCMIO_HANDLER;
import static io.wcm.testing.mock.wcmio.sling.ContextPlugins.WCMIO_SLING;
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

import java.io.IOException;
import java.util.Locale;

import org.apache.sling.api.resource.PersistenceException;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.SlingModelFilter;

import io.wcm.handler.media.spi.MediaHandlerConfig;
import io.wcm.handler.url.spi.UrlHandlerConfig;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;
import io.wcm.testing.mock.wcmio.caconfig.MockCAConfig;

/**
 * Sets up {@link AemContext} for unit tests in this application.
 */
public final class AppAemContext {

  public static final int ROOT_LEVEL = 2;
  public static final String LANGUAGE_ROOT = "/content/sample";
  public static final String CONTENT_ROOT = LANGUAGE_ROOT + "/en";
  public static final String DAM_ROOT = "/content/dam/sample";
  public static final String TEMPLATE_PATH = "/apps/app1/templates/template1";

  private AppAemContext() {
    // static methods only
  }

  public static AemContext newAemContext() {
    return new AemContextBuilder()
        .plugin(CACONFIG)
        .plugin(WCMIO_SLING, WCMIO_CACONFIG, WCMIO_HANDLER)
        .afterSetUp(SETUP_CALLBACK)
        .build();
  }

  /**
   * Custom set up rules required in all unit tests.
   */
  private static final AemContextCallback SETUP_CALLBACK = new AemContextCallback() {
    @Override
    public void execute(@NotNull AemContext context) throws PersistenceException, IOException {

      // mock services
      context.registerService(SlingModelFilter.class, new MockSlingModelFilter());

      // context path strategy
      MockCAConfig.contextPathStrategyAbsoluteParent(context, ROOT_LEVEL - 1, ROOT_LEVEL);

      // setup handler
      context.registerService(UrlHandlerConfig.class, new UrlHandlerConfigImpl());
      context.registerService(MediaHandlerConfig.class, new MediaHandlerConfigImpl());

      // create template
      context.create().resource(TEMPLATE_PATH,
          JCR_TITLE, "Template #1");

      // create root pages
      context.create().page(LANGUAGE_ROOT);
      context.create().page(CONTENT_ROOT, TEMPLATE_PATH,
          ImmutableValueMap.of(JCR_LANGUAGE, Locale.US.toLanguageTag()));

    }
  };

}
