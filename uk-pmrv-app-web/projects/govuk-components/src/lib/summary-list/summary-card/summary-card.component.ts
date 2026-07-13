import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, contentChild, input, TemplateRef } from '@angular/core';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'div[govuk-summary-card]',
  imports: [NgTemplateOutlet],
  template: `
    <div class="govuk-summary-card__title-wrapper">
      <h2 class="govuk-summary-card__title" [innerHTML]="title()"></h2>
      @if (this.actions(); as actions) {
        <ul class="govuk-summary-card__actions">
          <ng-container [ngTemplateOutlet]="actions" />
        </ul>
      }
    </div>
    <div class="govuk-summary-card__content">
      <ng-content />
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { '[class.govuk-summary-card]': 'govukSummaryCard' },
})
export class SummaryCardComponent {
  readonly title = input.required<string>();
  readonly actions = contentChild<TemplateRef<any>>('actions');

  readonly govukSummaryCard = true;
}
