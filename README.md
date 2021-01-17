<img src="https://wcm.io/images/favicon-16@2x.png"/> wcm.io WCM Core Components
======
[![Build Status](https://travis-ci.com/wcm-io/wcm-io-wcm-core-components.png?branch=develop)](https://travis-ci.com/wcm-io/wcm-io-wcm-core-components)
[![Code Coverage](https://codecov.io/gh/wcm-io/wcm-io-wcm-core-components/branch/develop/graph/badge.svg)](https://codecov.io/gh/wcm-io/wcm-io-wcm-core-components)

Enhances [AEM WCM Core Components][adobe-core-components] with wcm.io functionality.

Documentation: https://wcm.io/wcm/core-components/<br/>
Issues: https://wcm-io.atlassian.net/browse/WWCM<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://travis-ci.com/wcm-io/wcm-io-wcm-core-components/<br/>
Commercial support: https://wcm.io/commercial-support.html


## Build from sources

If you want to build wcm.io from sources make sure you have configured all [Maven Repositories](https://wcm.io/maven.html) in your settings.xml.

See [Travis Maven settings.xml](https://github.com/wcm-io/wcm-io-wcm-core-components/blob/master/.travis.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```

## Build and deploy to local AEM Instance

The script `build-deploy.sh` compiles everything and deploys the WCM Core Components including the Component Library to a local AEM instance running on port 4502.



[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
