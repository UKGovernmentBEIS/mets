import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

@Component({
  selector: 'app-aer-monitoring-plan-changes-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, NgFor, SharedModule, RouterLink],
  templateUrl: './monitoring-plan-changes-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerMonitoringPlanChangesSummaryTemplateComponent {
  @Input() data: AviationAerMonitoringPlanChanges;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
