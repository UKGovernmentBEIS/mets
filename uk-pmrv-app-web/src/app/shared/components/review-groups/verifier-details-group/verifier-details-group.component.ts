import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { AerVerificationReport, VerificationBodyEmissionSchemeDTO } from 'pmrv-api';

@Component({
  selector: 'app-verifier-details-group',
  standalone: false,
  templateUrl: './verifier-details-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsGroupComponent {
  readonly isEditable = input(false);
  readonly showVerifierDetails = input(true);
  readonly verificationReport = input<AerVerificationReport>(undefined);
  readonly emissionsTradingScheme = input<VerificationBodyEmissionSchemeDTO['emissionTradingScheme']>(undefined);
}
