Steps to update sample content when syncing with new features
-------------------------------------------------------------

1. copy component sample page
2. add teaser in library root page
3. replace everywhere:
```
core/wcm/components/ -> wcm-io/wcm/core/components/
/content/core-components-examples/ -> /content/wcmio-core-components-examples/
/conf/core-components-examples/settings/wcm/templates/ -> /conf/wcmio-core-components-examples/settings/wcm/templates/
``` 
4. add teaser_github_wcmio und teaser_github_adobe to component sample page
