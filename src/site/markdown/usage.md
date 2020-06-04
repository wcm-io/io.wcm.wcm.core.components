## WCM Core Component Usage

For each of the [AEM WCM Core Components][adobe-core-components] the wcm.io WCM Core Component module provides it's own component variant inheriting all features from the AEM WCM Core Components and enhancing them with wcm.io functionality.

Each wcm.io WCM Core Component has it's own resource type as documented in the [Component Overview][components].


### Proxy Component Pattern

To use them you have to follow the best practices as described in the [Adobe Component Guidelines][component-guidelines]. That means following the _Proxy Component Pattern_ you have to create a proxy component for each core component you want to use in your project.

Example:

* You want to use the title component with resource type `wcm-io/wcm/core/components/title/v2/title`
* Create your own proxy component e.g. at `/apps/my-application/components/title` with `sling:resourceSuperType=wcm-io/wcm/core/components/title/v2/title`
* Make sure to reference only your project-specific resource type `my-application/components/title` in your application and content

It is recommended to always reference the wcm.io Core Component Variant resource type even for those components, that are currently not enhanced by wcm.io but 100% inherited. It is possible that in future releases wcm.io feature enhancements will be required.

If you want to customize the components further, follow the [Customizing Core Components][customizing-core-components] guidelines.


### Component Library

To experiment with the wcm.io WCM Core Components install the [wcm.io WCM Core Component Library][component-library] in a local AEM instance.


### Setting up your project

If you want to start a new project it is recommended to use the [wcm.io Maven Archtype for AEM][maven-archetype-aem] and setting the `optionWcmioHandler=y`. The sets up a new AEM project including the setup for wcm.io Handler and includes some example usages of WCM Core Components.

When you want to switch an existing project make sure to include all dependency bundles and apply the required [system configuration][handler-configuration].


### Drop-in replacement

The wcm.io WCM Core Components are designed to be a drop-in replacement for the AEM WCM Core Components. All content properties stored by the original components can also be read by the wcm.io components, and the generated markup is the same.

Components that store link target information (e.g. Button, Title, Teaser, Image component) can read the link target information from the "old" property (e.g. `linkURL`). But once the data is edited and saved using the edit dialog, this old property is removed and replaced with the [Link Handler-specific properties][link-handler-link-properties]. That means that a switch back to the original components may require a manual content migration back to the "old" link properties.



[adobe-core-components]: https://github.com/adobe/aem-core-wcm-components
[components]: components.html
[component-library]: component-library.html
[component-guidelines]: https://docs.adobe.com/content/help/en/experience-manager-core-components/using/developing/guidelines.html
[link-handler-link-properties]: https://wcm.io/handler/link/usage.html#Link_properties_in_resource
[maven-archetype-aem]: https://wcm.io/tooling/maven/archetypes/aem/
[handler-configuration]: https://wcm.io/handler/configuration.html
[customizing-core-components]: https://docs.adobe.com/content/help/en/experience-manager-core-components/using/developing/customizing.html
