## About WCM Core Components

Enhances [AEM WCM Core Components][adobe-core-components] with wcm.io functionality.

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.wcm.core.components)](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.core.components/)


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
| 2.0.4-2.25.4                      | 2.25.4 - 2.30.2 and up           | AEM 6.5.17, AEMaaCS
| 2.0.x-2.23.2                      | 2.23.2, 2.24.6 and up            | AEM 6.5.17, AEMaaCS
| 1.14.0-2.23.2                     | 2.23.2 and up                    | AEM 6.5.17, AEMaaCS
| 1.13.0-2.22.6                     | 2.22.6 and up                    | AEM 6.5.14, AEMaaCS
| 1.12.0-2.20.0                     | 2.20.0 and up                    | AEM 6.5.13, AEMaaCS
| 1.11.0-2.19.0                     | 2.19.0 and up                    | AEM 6.5.7, AEMaaCS
| 1.10.0-2.18.6                     | 2.18.6 and up                    | AEM 6.5.7, AEMaaCS
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
| [AEM WCM Core Components (ZIP)](https://repo1.maven.org/maven2/com/adobe/cq/core.wcm.components.all/) | [![Maven Central](https://img.shields.io/maven-central/v/com.adobe.cq/core.wcm.components.all)](https://repo1.maven.org/maven2/com/adobe/cq/core.wcm.components.all/) |
| [wcm.io Sling Commons](https://repo1.maven.org/maven2/io/wcm/io.wcm.sling.commons/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.sling.commons)](https://repo1.maven.org/maven2/io/wcm/io.wcm.sling.commons/) |
| [wcm.io AEM Sling Models Extensions](https://repo1.maven.org/maven2/io/wcm/io.wcm.sling.models/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.sling.models)](https://repo1.maven.org/maven2/io/wcm/io.wcm.sling.models/) |
| [wcm.io WCM Commons](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.commons/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.wcm.commons)](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.commons/) |
| [wcm.io WCM Granite UI Extensions](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.ui.granite/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.wcm.ui.granite)](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.ui.granite/) |
| [wcm.io Handler Commons](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.commons/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.handler.commons)](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.commons/) |
| [wcm.io URL Handler](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.url/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.handler.url)](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.url/) |
| [wcm.io Media Handler](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.media/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.handler.media)](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.media/) |
| [wcm.io Link Handler](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.link/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.handler.link)](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.link/) |
| [wcm.io RichText Handler](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.richtext/) | [![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.handler.richtext)](https://repo1.maven.org/maven2/io/wcm/io.wcm.handler.richtext/) |


### Further Resources

* [adaptTo() 2019 Talk: Assets and Links in AEM Projects][adaptto-talk-2019-assets-links-in-aem-projects]
* [AEM Sites WKND Tutorial adapted for demonstrating wcm.io Features][aem-guides-wknd-wcmio]


### GitHub Repository

Sources: https://github.com/wcm-io/io.wcm.wcm.core.components


[apidocs]: core/apidocs/
[changelog]: changes.html
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
[aem-guides-wknd-wcmio]: https://github.com/wcm-io/aem-guides-wknd-wcmio