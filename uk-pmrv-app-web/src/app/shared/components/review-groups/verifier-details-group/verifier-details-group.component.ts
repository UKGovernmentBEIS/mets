import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AerVerificationReport } from 'pmrv-api';

@Component({
  selector: 'app-verifier-details-group',
  templateUrl: './verifier-details-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsGroupComponent {
  @Input() isEditable = false;
  @Input() showVerifierDetails = true;
  @Input() verificationReport: AerVerificationReport;
}
