import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { EtsComplianceRules } from 'pmrv-api';

@Component({
  selector: 'app-compliance-ets-group',
  templateUrl: './compliance-ets-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceEtsGroupComponent {
  @Input() isEditable = false;
  @Input() etsComplianceRules: EtsComplianceRules;
}
