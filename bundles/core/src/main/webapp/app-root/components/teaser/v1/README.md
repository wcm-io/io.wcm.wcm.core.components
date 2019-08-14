Teaser (v1)
====
Teaser component written in HTL, allowing definition of an image, title, rich text description and actions/links. Teaser variations can include some or all of these elements.

Extends the [Teaser AEM Sites Core Component][extends-component]

## Resource Type
```
wcm-io/wcm/core/components/teaser/v1/teaser
```

## Enhanced Features

* Inherits all features from its [super component][extends-component]
* Uses [wcm.io Media Handler][wcmio-handler-media] for processing the teaser image
* Allows to select media format(s) and auto-cropping in the content policy
* Allows to define responsive image settings in the content policy, allowing to select between image element with sizes and srcset attributes, or picture and sources elements
* Automatically customizes the in-place image editor cropping ratios to  the selected/the applications media formats
* Uses the enhanced Media Handler File Upload dialog widget with path field and media format validation
* Uses [wcm.io RichText Handler][wcmio-handler-richtext] for processing the description rich text
* Uses [wcm.io Link Handler][wcmio-handler-link] for processing the teaser and action links
* Use the Link Handler dialog widget for defining link type and link target

[extends-component]: https://github.com/adobe/aem-core-wcm-components/tree/master/content/src/content/jcr_root/apps/core/wcm/components/teaser/v1/teaser
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-link]: https://wcm.io/handler/link/
[wcmio-handler-richtext]: https://wcm.io/handler/richtext/
