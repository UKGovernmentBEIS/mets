import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'div[govukInsetText]',
  standalone: false,
})
export class InsetTextDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class')
  elementClass = 'govuk-inset-text';
}
