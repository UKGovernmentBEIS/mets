import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerComplianceMonitoringReportingRules } from 'pmrv-api';

@Component({
  selector: 'app-compliance-monitoring-group-template',
  templateUrl: './compliance-monitoring-group.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringGroupComponent {
  @Input() isEditable = false;
  @Input() compliance: AviationAerComplianceMonitoringReportingRules;
}
