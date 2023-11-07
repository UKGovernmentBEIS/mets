import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { MonitoringPlanVersion } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-plan-versions',
  templateUrl: './monitoring-plan-versions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanVersionsComponent implements OnInit {
  @Input() noBottomBorder: boolean;
  @Input() versions: MonitoringPlanVersion[];
  @Input() showCaption = true;

  data: { permitIdVersioned: string; endDate: string }[];

  tableColumns: GovukTableColumn[] = [
    { field: 'permitIdVersioned', header: '' },
    { field: 'endDate', header: '' },
  ];

  ngOnInit(): void {
    this.data = this.versions
      .map((plan) => ({
        permitIdVersioned: `${plan.permitId} v${plan.permitConsolidationNumber}`,
        endDate: plan.endDate,
      }))
      .reverse();
  }
}
