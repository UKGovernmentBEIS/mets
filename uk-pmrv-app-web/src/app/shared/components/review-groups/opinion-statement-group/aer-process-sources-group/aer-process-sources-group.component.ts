import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-aer-process-sources-group',
  template: `
    <h2 class="govuk-heading-m">Process sources</h2>
    <dl govuk-summary-list>
      <div govukSummaryListRow *ngFor="let processSource of processSources">
        <dt govukSummaryListRowKey>{{ processSource }}</dt>
        <dd govukSummaryListRowActions *ngIf="isEditable">
          <a govukLink [routerLink]="[deleteRouterLink, processSource]">Delete</a>
        </dd>
      </div>
    </dl>
    <div class="govuk-button-group" *ngIf="isEditable">
      <button govukSecondaryButton type="button" [routerLink]="addRouterLink">Add another process source</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerProcessSourcesGroupComponent {
  @Input() processSources: OpinionStatement['processSources'];
  @Input() isEditable: boolean;
  @Input() addRouterLink = 'add';
  @Input() deleteRouterLink = 'delete';
}
