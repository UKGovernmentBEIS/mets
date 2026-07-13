import { Directive } from '@angular/core';

@Directive({
  selector: 'div[govukSummaryListColumn]',
  host: { '[class]': 'className' },
})
export class SummaryListColumnDirective {
  className = 'govuk-summary-list__column';
}
