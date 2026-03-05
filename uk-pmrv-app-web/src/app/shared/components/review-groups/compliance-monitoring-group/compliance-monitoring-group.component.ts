import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ComplianceMonitoringReporting } from 'pmrv-api';

@Component({
  selector: 'app-compliance-monitoring-group',
  templateUrl: './compliance-monitoring-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringGroupComponent {
  @Input() isEditable = false;
  @Input() compliance: ComplianceMonitoringReporting;
}
