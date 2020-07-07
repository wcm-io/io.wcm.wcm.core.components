Navigation (v1)
====
Navigation component written in HTL that renders a website navigation tree.

Extends the [Navigation AEM Sites Core Component][extends-component]

## Resource Type
```
wcm-io/wcm/core/components/navigation/v1/navigation
```

## Enhanced Features

* Inherits all features from its [super component][extends-component]
* Uses [wcm.io Link Handler][wcmio-handler-link] for processing the navigation links
* Detection of navigation root
    * By default, [wcm.io URL Handler][wcmio-handler-url] is used for detecting the Site Root
    * Additionally, an relative path can be configured as "Navigation root" property, which is resolved relative to the automatic detected site root
    * Alternatively, an absolute path can be configured as "Navigation root" - it is rewritten to the current context and used to resolve the root page 

[extends-component]: https://github.com/adobe/aem-core-wcm-components/tree/master/content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation
[wcmio-handler-link]: https://wcm.io/handler/link/
[wcmio-handler-url]: https://wcm.io/handler/url/
