import { Directive } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListColumnActions]',
  host: { '[class]': 'className' },
})
export class SummaryListColumnActionsDirective {
  className = 'govuk-summary-list__actions';
}
