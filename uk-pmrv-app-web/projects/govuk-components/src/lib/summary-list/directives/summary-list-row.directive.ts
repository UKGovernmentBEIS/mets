import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'div[govukSummaryListRow]',
  standalone: false,
})
export class SummaryListRowDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class') className = 'govuk-summary-list__row';
}
