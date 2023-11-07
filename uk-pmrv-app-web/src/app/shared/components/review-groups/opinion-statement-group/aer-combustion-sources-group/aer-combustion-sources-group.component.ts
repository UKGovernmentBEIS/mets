import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-aer-combustion-sources-group',
  template: `
    <h2 class="govuk-heading-m">Source streams</h2>
    <dl govuk-summary-list>
      <div govukSummaryListRow *ngFor="let combustionSource of combustionSources">
        <dt govukSummaryListRowKey>{{ combustionSource }}</dt>
        <dd govukSummaryListRowActions *ngIf="isEditable">
          <a govukLink [routerLink]="[deleteRouterLink, combustionSource]">Delete</a>
        </dd>
      </div>
    </dl>
    <div class="govuk-button-group" *ngIf="isEditable">
      <button govukSecondaryButton type="button" [routerLink]="addRouterLink">Add another combustion source</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerCombustionSourcesGroupComponent {
  @Input() combustionSources: OpinionStatement['combustionSources'];
  @Input() isEditable: boolean;
  @Input() addRouterLink = 'add';
  @Input() deleteRouterLink = 'delete';
}
