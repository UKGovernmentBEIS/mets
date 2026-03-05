import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-aer-regulated-activities-group',
  template: `
    <h2 class="govuk-heading-m">Regulated activities</h2>
    <dl govuk-summary-list>
      <div govukSummaryListRow *ngFor="let regulatedActivity of regulatedActivities">
        <dt govukSummaryListRowKey>{{ regulatedActivity | regulatedActivityType }} ({{ regulatedActivity | gas }})</dt>
        <dd govukSummaryListRowActions *ngIf="isEditable">
          <a govukLink [routerLink]="[deleteRouterLink, regulatedActivity]">Delete</a>
        </dd>
      </div>
    </dl>
    <div class="govuk-button-group" *ngIf="isEditable">
      <button govukSecondaryButton [routerLink]="addRouterLink" type="button">Add another regulated activity</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerRegulatedActivitiesGroupComponent {
  @Input() regulatedActivities: OpinionStatement['regulatedActivities'];
  @Input() isEditable: boolean;
  @Input() addRouterLink = 'add';
  @Input() deleteRouterLink = 'delete';
}
