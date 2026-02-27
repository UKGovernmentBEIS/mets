import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListRowActions]',
  standalone: false,
})
export class SummaryListRowActionsDirective {
  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class') className = 'govuk-summary-list__actions';
}
