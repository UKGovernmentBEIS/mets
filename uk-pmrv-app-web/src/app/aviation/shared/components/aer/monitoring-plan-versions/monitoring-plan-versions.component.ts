import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMonitoringPlanVersion } from 'pmrv-api';

@Component({
  selector: 'app-aer-monitoring-plan-versions',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, NgFor, SharedModule],
  templateUrl: './monitoring-plan-versions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerMonitoringPlanVersionsComponent {
  @Input() planVersions: AviationAerMonitoringPlanVersion[];
}
