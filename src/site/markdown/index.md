## About WCM Core Components

Enhances [AEM Sites Core Components][adobe-core-components] with wcm.io functionality.

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

A detailed documentation for each component can be found in the [Component Overview][components].


### AEM Version Support Matrix

|wcm.io WCM Core Components version | AEM Sites Core Component version | AEM version supported
|-----------------------------------|----------------------------------|-------------------------------------------
|1.0.x                              | 2.5.0                            | AEM 6.3 SP3, AEM 6.4 SP2, AEM 6.5 and up


### Dependencies

To use this module you have to deploy also:

|---|---|---|
| [AEM Sites Core Components (ZIP)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all) |
| [wcm.io Sling Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.commons) |
| [wcm.io AEM Sling Models Extensions](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.sling.models) |
| [wcm.io WCM Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.commons) |
| [wcm.io WCM Granite UI Extensions](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.wcm.ui.granite) |
| [wcm.io Handler Commons](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.commons) |
| [wcm.io URL Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.url) |
| [wcm.io Media Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.media) |
| [wcm.io Link Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.link) |
| [wcm.io RichText Handler](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.wcm/io.wcm.handler.richtext) |


[apidocs]: core/apidocs/
[changelog]: changes-report.html
[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
[wcmio-handler]: https://wcm.io/handler/
[wcmio-handler-url]: https://wcm.io/handler/url/
[wcmio-handler-link]: https://wcm.io/handler/link/
[wcmio-handler-media]: https://wcm.io/handler/media/
[wcmio-handler-richtext]: https://wcm.io/handler/richtext/
[usage]: usage.html
[component-library]: component-library.html
[components]: components.html