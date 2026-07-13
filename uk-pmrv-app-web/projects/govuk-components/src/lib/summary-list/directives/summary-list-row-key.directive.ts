import { Directive } from '@angular/core';

@Directive({
  selector: 'dt[govukSummaryListRowKey]',
  host: { '[class]': 'className' },
})
export class SummaryListRowKeyDirective {
  className = 'govuk-summary-list__key';
}
