import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListRowValue]',
  standalone: false,
})
export class SummaryListRowValueDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class') className = 'govuk-summary-list__value';
}
