## WCM Core Component Usage

For each of the [AEM Sites Core Components][adobe-core-components] the wcm.io WCM Core Component module provides it's own component variant inheriting all features from the AEM Sites Core Components and enhancing them with wcm.io functionality.

Each wcm.io WCM Core Component has it's own resource type as documented in the [Component Overview][components].


### Proxy Component Pattern

To use them you have to follow the best practices as described in the [Adobe Component Guidelines][component-guidelines]. That means following the _Proxy Component Pattern_ you have to create a proxy component for each core component you want to use in your project.

Example:

* You want to use the title component with resource type `wcm-io/wcm/core/components/title/v2/title`
* Create your own proxy component e.g. at `/apps/my-application/components/title` with `sling:resourceSuperType=wcm-io/wcm/core/components/title/v2/title`
* Make sure to reference only your project-specific resource type `my-application/components/title` in your application and content

It is recommended to always reference the wcm.io Core Component Variant resource type even for those components, that are currently not enhanced by wcm.io but 100% inherited. It is possible that in future releases wcm.io feature enhancements will be required.


### Component Library

To experiment with the wcm.io WCM Core Components install the [wcm.io WCM Core Component Library][component-library] in a local AEM instance.


### Drop-in replacement

Are the wcm.io WCM Core Components a drop-in replacement for the AEM Sites Core Components? In most cases, yes. But the dialogs that store link information (e.g. Button, Image, Teaser components) store the target link data in [different attributes][link-handler-link-properties] than the original core components. So the link data properties have to be converted when switching to wcm.io WCM Core Components.



[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
[components]: components.html
[component-library]: component-library.html
[component-guidelines]: https://docs.adobe.com/content/help/en/experience-manager-core-components/using/developing/guidelines.html
[link-handler-link-properties]: https://wcm.io/handler/link/usage.html#Link_properties_in_resource