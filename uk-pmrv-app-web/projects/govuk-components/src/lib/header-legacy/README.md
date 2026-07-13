The header components are designed to add header content to a full screen app.
To set up a govuk header we use four components:

1. `<govuk-header-legacy>` which acts as a structural container for our content and navigation.
2. `<govuk-header-nav-list-legacy>` which represents the navigation of the application.
3. `<govuk-header-actions-list>` which represents the actions aligned to the right.
4. `<govuk-skip-link>` which represents the skip link for keyboard users.

### Specifying the service name and navigation

To define the service name you can use a title input property. The navigation should be placed inside `<govuk-header>`.
The `logoSvg` property can be used to set the logo SVG of the application. Usually the
`govuk-header-logotype-crown.svg`.

The navigation should be wrapped in a `<govuk-header-nav-list>`. The `identifier` property can be used
to add id attribute to the navigation list. The `ariaLabel` property can be used to add aria-label
attribute to the navigation list. The `menuButtonAriaLabel` property can be used to add aria-label
to the menu button which can be found in smaller screens where the navigation is not visible.

The actions should be wrapped in a `<govuk-header-actions-list>`.

The `govukLink` directive is required with input `header` for every link inside the `<govuk-header-nav-list>` container
in order to apply the required styling.

The following is an example of a valid header container:

```html
<govuk-header-legacy title="NETZ">
  <govuk-header-actions-list>
    <a class="govuk-header__link">Profile</a>
  </govuk-header-actions-list>
  <govuk-header-nav-list-legacy identifier="navigation" ariaLabel="Top Level Navigation">
    <a govukLink="header" href="/">Home</a>
  </govuk-header-nav-list-legacy>
</govuk-header-legacy>
```
