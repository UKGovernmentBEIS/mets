import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-review-group-decision-summary',
  template: `
    <h2 app-summary-header class="govuk-heading-m">Decision Summary</h2>
    <dl govuk-summary-list class="govuk-summary-list--no-border summary-list--edge-border">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Decision status</dt>
        <dd govukSummaryListRowValue>{{ decisionData.type | reviewGroupDecision }}</dd>
      </div>
      <div govukSummaryListRow *ngIf="decisionData?.details?.notes">
        <dt govukSummaryListRowKey>Notes</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ decisionData.details.notes }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewGroupDecisionSummaryComponent {
  @Input() decisionData: any;
}
