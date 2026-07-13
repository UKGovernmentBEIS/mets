## Back link

Use the back link component to help users go back to the previous page in a multi-page transaction.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/back-link/).

Use the component at the beginning of the wizard, in order to capture previous routes.
The first route it finds when it's used keeps it hidden. That route is its root route.

Use the optional inverse input `[inverse="true"]` for back links on dark background. (https://design-system.service.gov.uk/components/back-link/#back-links-on-dark-backgrounds)

### Example

```html
<govuk-back-link></govuk-back-link>
```

```html
<govuk-back-link [inverse]="true"></govuk-back-link>
```
