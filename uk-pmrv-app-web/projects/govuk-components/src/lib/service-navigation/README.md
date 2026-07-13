## Service navigation

Service navigation helps users understand that they’re using your service and lets them navigate around your service.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/service-navigation/).

### Inputs

- `serviceName` - The name of the service.
- `serviceUrl` - The URL of the service homepage.
- `navigationItems` - An array of navigation items, each with an `href` and `text`.
- `ariaLabel` - The ARIA label for the service information section. Defaults to `Service information`.
- `menuButtonText` - The text for the menu button. Defaults to `Menu`.
- `menuButtonLabel` - The ARIA label for the menu button. Defaults to `Menu`.
- `navigationId` - The ID for the navigation element. Defaults to `navigation`.
- `navigationLabel` - The ARIA label for the navigation element. Defaults to `Menu`.
- `collapseNavigationOnMobile` - Whether to collapse the navigation on mobile. Defaults to `true`.

### Examples

```html
<govuk-service-navigation
  serviceName="Service Name"
  serviceUrl="/"
  [navigationItems]="[
    { href: '/page-1', text: 'Page 1' },
    { href: '/page-2', text: 'Page 2' }
  ]" />
```
