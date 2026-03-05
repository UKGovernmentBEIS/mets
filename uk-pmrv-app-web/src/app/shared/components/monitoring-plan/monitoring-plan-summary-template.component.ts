import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AerMonitoringPlanDeviation, OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-plan-summary-template',
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <h3 class="govuk-heading-s" *ngIf="headingSmall; else h2Heading">
      Changes not covered in the approved monitoring plans
    </h3>
    <ng-template #h2Heading>
      <h2 class="govuk-heading-m">Changes not covered in the approved monitoring plans</h2>
    </ng-template>
    <dl govuk-summary-list [class]="cssClass">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Changes reported by the operator</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ planDeviation.details }}</dd>
        <dd govukSummaryListRowActions>
          <a *ngIf="isEditable" govukLink routerLink="..">Change</a>
        </dd>
      </div>
      <div govukSummaryListRow *ngIf="opinionStatement">
        <dt govukSummaryListRowKey>Changes reported by the verifier</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>
          {{
            opinionStatement?.additionalChangesNotCovered && opinionStatement?.additionalChangesNotCoveredDetails
              ? opinionStatement.additionalChangesNotCoveredDetails
              : 'No'
          }}
        </dd>
        <dd govukSummaryListRowActions>
          <a *ngIf="opinionStatementEditable" govukLink routerLink="../additional-changes">Change</a>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() opinionStatementEditable = false;
  @Input() headingSmall = true;
  @Input() cssClass: string;
  @Input() planDeviation: AerMonitoringPlanDeviation;
  @Input() opinionStatement?: OpinionStatement;
}
