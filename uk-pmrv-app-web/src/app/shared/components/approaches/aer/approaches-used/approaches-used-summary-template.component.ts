import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { AerMonitoringApproachEmissions, PermitMonitoringApproachSection } from 'pmrv-api';

@Component({
  selector: 'app-approaches-used-summary-template',
  template: `
    <dl govuk-summary-list [class]="cssClass">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Approaches used</dt>
        <dd govukSummaryListRowValue>
          <span *ngFor="let monitoringApproach of approaches">
            {{ monitoringApproach | monitoringApproachDescription }}<br/>
          </span>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesUsedSummaryTemplateComponent implements OnInit {
  @Input() cssClass: string;
  @Input() monitoringApproaches: { [key: string]: AerMonitoringApproachEmissions };

  approaches: PermitMonitoringApproachSection['type'][];

  ngOnInit(): void {
    this.approaches = monitoringApproachTypeOptions.filter(
      (option) => this.monitoringApproaches[option]?.type || this.monitoringApproaches[option] !== undefined,
    );
  }
}
