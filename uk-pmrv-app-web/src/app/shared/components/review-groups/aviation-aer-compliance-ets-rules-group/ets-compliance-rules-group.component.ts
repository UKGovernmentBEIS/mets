import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

@Component({
  selector: 'app-ets-compliance-rules-group',
  templateUrl: './ets-compliance-rules-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EtsComplianceRulesGroupComponent {
  @Input() isEditable = false;
  @Input() etsComplianceRules: AviationAerEtsComplianceRules;
  @Input() queryParams: Params = {};
}
