Image (v3)
====
Image component written in HTL that renders an adaptive image.

Extends the [Image AEM Sites Core Component][extends-component]

## Resource Type
```
wcm-io/wcm/core/components/image/v3/image
```

## Enhanced Features

* Inherits all features from its [super component][extends-component]
* Uses [wcm.io Media Handler][wcmio-handler-media] for processing the image
* Allows to select media format(s) and auto-cropping in the content policy
* Automatically customizes the in-place image editor cropping ratios to  the selected/the applications media formats
* Uses the enhanced Media Handler File Upload dialog widget with path field and media format validation
* Uses [wcm.io Link Handler][wcmio-handler-link] for processing the image link
* Uses the Link Handler dialog widget for defining link type and link target


[extends-component]: https://github.com/adobe/aem-core-wcm-components/tree/master/content/src/content/jcr_root/apps/core/wcm/components/image/v3/image
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-link]: https://wcm.io/handler/link/
