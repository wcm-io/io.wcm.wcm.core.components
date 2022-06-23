Steps to update sample content when syncing with new features
-------------------------------------------------------------

1. copy component sample page
2. remove _jcr_content/image subfolder
3. add teaser in library root page
4. replace everywhere:
```
core/wcm/components/ -> wcm-io/wcm/core/components/
core-components-examples/components/<component> -> wcm-io/wcm/core/components/<component>/<version>/<component>
/content/core-components-examples/ -> /content/wcmio-core-components-examples/
/conf/core-components-examples/settings/wcm/templates/ -> /conf/wcmio-core-components-examples/settings/wcm/templates/
``` 
4. add teaser_github_wcmio und teaser_github_adobe to component sample page
5. check if components starting with `core-components-examples/components/` have to be replaced with `wcm-io/wcm/core/components/.../vX/...`
