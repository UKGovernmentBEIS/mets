import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params } from '@angular/router';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

@Component({
  selector: 'app-verify-emissions-reduction-claim-group',
  templateUrl: './verify-emissions-reduction-claim-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifyEmissionsReductionClaimGroupComponent {
  @Input() isEditable = false;
  @Input() emissionsReductionClaimVerification: AviationAerEmissionsReductionClaimVerification;
  @Input() queryParams: Params = {};
}
