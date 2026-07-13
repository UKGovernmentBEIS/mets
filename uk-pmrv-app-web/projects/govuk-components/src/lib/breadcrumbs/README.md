## Breadcrumbs

The breadcrumbs component helps users to understand where they are within a website’s structure and move between levels.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/breadcrumbs/).

Only anchor links decorated with `govukLink="breadcrumb"` should be used as the component's content.
These links form the breadcrumb hierarchy, from higher to lower.

The component renders a `<nav aria-label="Breadcrumb">` landmark, matching `govuk-frontend`.

#### Inputs

- `[inverse]` (default `false`) — use for breadcrumbs on a dark background. (https://design-system.service.gov.uk/components/breadcrumbs/#breadcrumbs-on-dark-backgrounds)
- `[collapseOnMobile]` (default `true`) — when `true`, the breadcrumbs collapse to the first and last item only on tablet breakpoint and below.
- `[labelText]` (default `"Breadcrumb"`) — plain-text label identifying the landmark to screen readers.

### Example

```html
<govuk-breadcrumbs>
  <a govukLink="breadcrumb" href="#">Home</a>
  <a govukLink="breadcrumb" href="#">Travel abroad</a>
  <a govukLink="breadcrumb" href="#">Environment</a>
</govuk-breadcrumbs>
```

```html
<govuk-breadcrumbs [inverse]="true">
  <a govukLink="breadcrumb" href="#">Home</a>
  <a govukLink="breadcrumb" href="#">Travel abroad</a>
  <a govukLink="breadcrumb" href="#">Environment</a>
</govuk-breadcrumbs>
```
