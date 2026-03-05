import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SummaryItem } from 'govuk-components';

import { EnvironmentalPermitsAndLicences } from 'pmrv-api';

@Component({
  selector: 'app-permits-summary-template',
  template: `
    <dl
      *ngFor="let detail of _environmentalPermitsAndLicences"
      [details]="detail"
      appGroupedSummaryList
      govuk-summary-list
      [hasBottomBorder]="hasBottomBorder"></dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitsSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  _environmentalPermitsAndLicences: SummaryItem[][];
  @Input()
  set environmentalPermitsAndLicences(task: EnvironmentalPermitsAndLicences) {
    this._environmentalPermitsAndLicences = task
      ? task.exist
        ? task.envPermitOrLicences.map((permit) => [
            { key: 'Type', value: permit.type },
            { key: 'Number', value: permit.num },
            { key: 'Issuing authority', value: permit.issuingAuthority },
            { key: 'Permit holder', value: permit.permitHolder },
          ])
        : [[{ key: 'Any other environmental permits or licences', value: 'No' }]]
      : null;
  }
}
