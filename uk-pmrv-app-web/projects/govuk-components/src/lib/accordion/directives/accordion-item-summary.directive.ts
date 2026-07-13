import { Directive } from '@angular/core';

@Directive({
  selector: '[govukAccordionItemSummary]',
  host: { '[class]': 'elementClass' },
})
export class AccordionItemSummaryDirective {
  elementClass = 'govuk-accordion__section-summary-focus';
}
