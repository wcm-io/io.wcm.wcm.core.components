<img src="https://wcm.io/images/favicon-16@2x.png"/> wcm.io WCM Core Components
======
[![Build](https://github.com/wcm-io/io.wcm.wcm.core.components/workflows/Build/badge.svg?branch=develop)](https://github.com/wcm-io/io.wcm.wcm.core.components/actions?query=workflow%3ABuild+branch%3Adevelop)
[![Maven Central](https://img.shields.io/maven-central/v/io.wcm/io.wcm.wcm.core.components)](https://repo1.maven.org/maven2/io/wcm/io.wcm.wcm.core.components/)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=wcm-io_io.wcm.wcm.core.components&metric=coverage)](https://sonarcloud.io/summary/new_code?id=wcm-io_io.wcm.wcm.core.components)

Enhances [AEM WCM Core Components][adobe-core-components] with wcm.io functionality.

Documentation: https://wcm.io/wcm/core-components/<br/>
Issues: https://github.com/wcm-io/io.wcm.wcm.core.components/issues<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://github.com/wcm-io/io.wcm.wcm.core.components/actions<br/>
Commercial support: https://wcm.io/commercial-support.html


## Build from sources

If you want to build wcm.io from sources make sure you have configured all [Maven Repositories](https://wcm.io/maven.html) in your settings.xml.

See [Maven Settings](https://github.com/wcm-io/io.wcm.wcm.core.components/blob/develop/.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```

## Build and deploy to local AEM Instance

The script `build-deploy.sh` compiles everything and deploys the WCM Core Components including the Component Library to a local AEM instance running on port 4502.



[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
