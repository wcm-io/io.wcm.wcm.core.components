## About WCM Core Components

Enhances [AEM WCM Core Components][adobe-core-components] with wcm.io functionality.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.core.components/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.core.components)


### Documentation

* [Usage][usage]
* [Component Overview][components]
* [Component Library][component-library]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

Adobe provides AEM developers with a set of [Core Components][adobe-core-components] that should be the starting point for every AEM application. The wcm.io WCM Core Components enhance the Adobe Core Components and introduces centrally managed handling for links, media and rich text by leveraging the [wcm.io Handler][wcmio-handler] modules. You can quickly try them out in a local AEM instance by installing the [wcm.io WCM Core Component Library][component-library].

Features added to the Core Components:

* Provides URL handling and externalization by [wcm.io URL Handler][wcmio-handler-url]
* Adds central link handling provided by [wcm.io Link Handler][wcmio-handler-link]
* Adds central image and asset handling with special support for responsive images provided by [wcm.io Media Handler][wcmio-handler-media]
* Adds central rich text support with a customized link RTE plugin provided by [wcm.io RichText Handler][wcmio-handler-richtext]
* Adds a new "Responsive Image" Core Component that uses standard HTML5 markup
* Add transparent support for Dynamic Media based on [wcm.io Media Handler Dynamic Media Support][wcmio-handler-media-dynamicmedia]

A detailed documentation for each component can be found in the [Component Overview][components].

There is currently no added wcm.io support for the Core Component AMP extensions.

### AEM Version Support Matrix

|wcm.io WCM Core Components version | AEM Sites Core Component version | AEM version supported
|-----------------------------------|----------------------------------|---------------------------------------------
| 1.10.0-2.17.12                    | 2.17.12 and up                   | AEM 6.5.6, AEMaaCS
| 1.9.2-2.17.12                     | 2.17.12 and up                   | AEM 6.4.8.4, AEM 6.5.6, AEMaaCS
| 1.9.0-2.17.2                      | 2.17.x and up                    | AEM 6.4.8.4, AEM 6.5.6, AEMaaCS
| 1.8.x-2.15.0                      | 2.15.x, 2.16.x                   | AEM 6.4.8.1, AEM 6.5.6, AEMaaCS
| 1.7.x-2.13.0                      | 2.13.x, 2.14.x                   | AEM 6.4.8.1, AEM 6.5.6, AEMaaCS
| 1.6.x-2.12.0                      | 2.12.x                           | AEM 6.4.8.1, AEM 6.5.5, AEMaaCS
| 1.5.x-2.11.0                      | 2.11.x                           | AEM 6.4.8.1, AEM 6.5.5, AEMaaCS
| 1.4.x-2.10.0                      | 2.10.0                           | AEM 6.4.8.1, AEM 6.5.5, AEMaaCS
| 1.3.x-2.9.0                       | 2.9.0                            | AEM 6.4.8, AEM 6.5.4, AEMaaCS
| 1.2.x-2.8.0                       | 2.8.0                            | AEM 6.3.3, AEM 6.4.4, AEM 6.5 and up
| 1.2.x-2.7.0                       | 2.7.0                            | AEM 6.3.3, AEM 6.4.4, AEM 6.5 and up
| 1.1.x-2.6.0                       | 2.6.0                            | AEM 6.3.3, AEM 6.4.4, AEM 6.5 and up
| 1.0.x-2.5.0                       | 2.5.0                            | AEM 6.3.3, AEM 6.4.2, AEM 6.5 and up
| 1.0.x-2.4.0                       | 2.4.0                            | AEM 6.3.3, AEM 6.4.2, AEM 6.5 and up


### Dependencies

To use this module you have to deploy also:

|---|---|---|
| [AEM WCM Core Components (ZIP)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all) |
| [wcm.io Sling Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons) |
| [wcm.io AEM Sling Models Extensions](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models) |
| [wcm.io WCM Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons) |
| [wcm.io WCM Granite UI Extensions](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite) |
| [wcm.io Handler Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons) |
| [wcm.io URL Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url) |
| [wcm.io Media Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media) |
| [wcm.io Link Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link) |
| [wcm.io RichText Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext) |


### Further Resources

* [adaptTo() 2019 Talk: Assets and Links in AEM Projects][adaptto-talk-2019-assets-links-in-aem-projects]


### GitHub Repository

Sources: https://github.com/wcm-io/io.wcm.wcm.core.components


[apidocs]: core/apidocs/
[changelog]: changes-report.html
[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
[wcmio-handler]: https://wcm.io/handler/
[wcmio-handler-url]: https://wcm.io/handler/url/
[wcmio-handler-link]: https://wcm.io/handler/link/
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-media-dynamicmedia]: https://wcm.io/handler/media/dynamic-media.html
[wcmio-handler-richtext]: https://wcm.io/handler/richtext/
[usage]: usage.html
[component-library]: component-library.html
[components]: components.html
[adaptto-talk-2019-assets-links-in-aem-projects]: https://adapt.to/2019/en/schedule/assets-and-links-in-aem-projects.html
