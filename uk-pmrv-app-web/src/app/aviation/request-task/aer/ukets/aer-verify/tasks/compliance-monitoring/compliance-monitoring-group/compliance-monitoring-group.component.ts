import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerComplianceMonitoringReportingRules } from 'pmrv-api';

@Component({
  selector: 'app-compliance-monitoring-group-template',
  imports: [SharedModule, RouterModule],
  templateUrl: './compliance-monitoring-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringGroupComponent {
  @Input() isEditable = false;
  @Input() compliance: AviationAerComplianceMonitoringReportingRules;
}
