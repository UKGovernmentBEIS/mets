## Radio component

Radio component is designed as a custom form control that follows the govuk style guidelines.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/radios/).

The radio component supports conditional content for options. We can achieve that by providing content inside
each `govuk-radio-option`, marked with the `govukConditionalContent` directive.
Child controls are automatically disabled when the content is hidden.

Attribute `govukRadioAdditionalContent` can be used to project additional content that is positioned after the hint and before the error messages.

### Inputs

- `legend` - A heading as a title.
- `legendSize` - A `LegendSizeType`. Defaults to `medium`.
- `hint` - A hint with smaller font as a description.
- `radioSize` - Defaults to `large`. Also accepts `medium`.
- `isInline` - A boolean flag controlling whether the radio button will be inline or not. Defaults to `false`.

`govuk-radio-option`:

- `value` - The option value.
- `label` - The option text.
- `hint` - an optional hint text.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-radio formControlName="exampleControlName"></div>
</form>
```

```html
<div govuk-radio [formControl]="exampleControl"></div>
```

```html
<div govuk-radio [formControl]="exampleControl">
  <ng-container govukRadioAdditionalContent>
    <govuk-details summary="Something is going on!">
      <div>Something something</div>
      <strong>Something else</strong>
    </govuk-details>
  </ng-container>
  <govuk-radio-option label="Example 1" [value]="1" />
  <govuk-radio-option label="Example 2" [value]="2" />
</div>
```
