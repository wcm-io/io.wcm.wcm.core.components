wcm.io Responsive Image (v2)
====
Image component written in HTL that renders an responsive image using standard HTML5 markup.

## Resource Type
```
wcm/core/components/wcmio/responsiveimage/v1/responsiveimage
```

## Features

* Markup and features are similar to the [Image Core Component][image-component], except the image element itself.
* Uses [wcm.io Media Handler][wcmio-handler-media] for processing the image
* Allows to select media format(s) and auto-cropping in the content policy
* Allows to define responsive image settings in the content policy, allowing to select between image element with sizes and srcset attributes, or picture and sources elements
* Automatically customizes the in-place image editor cropping ratios to  the selected/the applications media formats
* Uses the enhanced Media Handler File Upload dialog widget with path field and media format validation
* Uses [wcm.io Link Handler][wcmio-handler-link] for processing the image link
* Uses the Link Handler dialog widget for defining link type and link target

[image-component]: https://github.com/adobe/aem-core-wcm-components/tree/master/content/src/content/jcr_root/apps/core/wcm/components/image/v2/image
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-link]: https://wcm.io/handler/link/
