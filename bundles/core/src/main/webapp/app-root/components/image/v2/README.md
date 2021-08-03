Image (v2) - deprecated
====
Image component written in HTL that renders an adaptive image.

Extends the [Image AEM Sites Core Component][extends-component]

## Resource Type
```
wcm-io/wcm/core/components/image/v2/image
```

## Enhanced Features

* Inherits all features from its [super component][extends-component]
* Uses [wcm.io Media Handler][wcmio-handler-media] for processing the image
* There is no support for dynamic media in this component. If you want to use dynamic media, use the "wcm.io Responsive Image" core component.
* Allows to select media format(s) and auto-cropping in the content policy
* Automatically customizes the in-place image editor cropping ratios to  the selected/the applications media formats
* Uses the enhanced Media Handler File Upload dialog widget with path field and media format validation
* Uses [wcm.io Link Handler][wcmio-handler-link] for processing the image link
* Uses the Link Handler dialog widget for defining link type and link target

Please note: This component does not support all features from the wcm.io Media Handler, especially not its responsive image handling. It emulates the markup and adaptive image handling from the [Image AEM Sites Core Component][extends-component], and only uses the Media Handler internally.

For HTML5 standards-based reponsive image handling use the [wcm.io Reponsive Image][wcmio-core-component-responsive-image] core component.


[extends-component]: https://github.com/adobe/aem-core-wcm-components/tree/master/content/src/content/jcr_root/apps/core/wcm/components/image/v2/image
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-link]: https://wcm.io/handler/link/
[wcmio-core-component-responsive-image]: https://github.com/wcm-io/wcm-io-wcm-core-components/tree/develop/bundles/core/src/main/webapp/app-root/components/wcmio/responsiveimage/v1