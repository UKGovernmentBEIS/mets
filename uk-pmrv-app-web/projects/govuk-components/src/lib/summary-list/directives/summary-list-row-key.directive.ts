import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dt[govukSummaryListRowKey]',
  standalone: false,
})
export class SummaryListRowKeyDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class') className = 'govuk-summary-list__key';
}
