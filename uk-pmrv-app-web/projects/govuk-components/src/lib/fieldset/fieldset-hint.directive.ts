import { Directive } from '@angular/core';

@Directive({ selector: 'div[govukFieldsetHint]', host: { '[class.govuk-hint]': 'hintClass' } })
export class FieldsetHintDirective {
  readonly hintClass = true;
}
