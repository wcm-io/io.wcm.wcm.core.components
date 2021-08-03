package io.wcm.wcm.core.components.impl.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaArgs;
import io.wcm.handler.media.MediaBuilder;
import io.wcm.handler.media.MediaHandler;
import io.wcm.handler.media.impl.ImageFileServlet;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.wcm.commons.contenttype.ContentType;
import io.wcm.wcm.commons.contenttype.FileExtension;
import io.wcm.wcm.core.components.impl.models.v2.ImageV2Impl;

/**
 * This servlet acts as a "proxy" for the {@link io.wcm.handler.media.impl.ImageFileServlet}
 * that emulates support for the "widths" attributes of the core component and maps them
 * to media handler-URLs internally.
 * <p>
 * Expected selectors:
 * </p>
 * <ol>
 * <li>imgwidth</li>
 * <li>{width}</li>
 * </ol>
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
    resourceTypes = { ImageV2Impl.RESOURCE_TYPE },
    selectors = ImageWidthProxyServlet.SELECTOR,
    extensions = { FileExtension.JPEG, FileExtension.PNG, FileExtension.GIF },
    methods = "GET")
public class ImageWidthProxyServlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 1L;

  @Reference
  private MimeTypeService mimeTypeService;

  /**
   * Selector
   */
  public static final String SELECTOR = "imgwidth";

  private static final Logger log = LoggerFactory.getLogger(ImageWidthProxyServlet.class);

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
    long width = getWidth(request);

    // build media URL and validate it
    Media media = buildMedia(request, width);
    if (!media.isValid()) {
      log.debug("Unable to resolve media: {}, width={}", media.getMediaRequest().getMediaRef(), width);
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // if media url used ImageFileServlet forward request
    if (usesImageFileServlet(media.getUrl())) {
      log.debug("Forward to {}", media.getUrl());
      request.getRequestDispatcher(media.getUrl()).forward(request, response);
    }
    else {
      // otherwise it points directly to a binary in repository, stream it directly
      log.debug("Stream binary content from {}", media.getRendition().getPath());
      response.setContentType(getMimeType(request));
      Resource resource = AdaptTo.notNull(media.getRendition(), Resource.class);
      try (InputStream is = resource.adaptTo(InputStream.class)) {
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        IOUtils.copy(is, bos);
        bos.flush();
      }
    }
  }

  private long getWidth(SlingHttpServletRequest request) {
    String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors.length >= 2) {
      return NumberUtils.toLong(selectors[1]);
    }
    return 0;
  }

  private Media buildMedia(SlingHttpServletRequest request, long width) {
    Resource resource = request.getResource();
    MediaHandler mediaHandler = AdaptTo.notNull(request, MediaHandler.class);
    MediaBuilder builder = mediaHandler.get(resource);
    if (width > 0) {
      builder.args(new MediaArgs().fixedWidth(width));
    }
    return builder.build();
  }

  private boolean usesImageFileServlet(String mediaUrl) {
    return StringUtils.contains(mediaUrl, "." + ImageFileServlet.SELECTOR + ".");
  }

  private String getMimeType(SlingHttpServletRequest request) {
    String mimeType = mimeTypeService.getMimeType(request.getRequestPathInfo().getExtension());
    return StringUtils.defaultString(mimeType, ContentType.OCTET_STREAM);
  }

}
