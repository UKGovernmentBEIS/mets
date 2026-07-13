import { Directive } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListColumnValue]',
  host: { '[class]': 'className' },
})
export class SummaryListColumnValueDirective {
  className = 'govuk-summary-list__value';
}
