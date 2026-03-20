import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'span[govukFieldsetHint]',
  standalone: false,
})
export class FieldsetHintDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class.govuk-hint') readonly hintClass = true;
}
