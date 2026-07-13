import { Directive } from '@angular/core';

@Directive({
  selector: 'div[govukInsetText]',
  host: { '[class]': 'elementClass' },
})
export class InsetTextDirective {
  elementClass = 'govuk-inset-text';
}
