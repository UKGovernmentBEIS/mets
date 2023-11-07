import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import { VerificationBodyDetailsInfoTemplateComponent } from '@aviation/shared/components/aer-verify/verification-body-details-info-template/verification-body-details-info-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifierDetails, VerificationBodyDetails } from 'pmrv-api';

@Component({
  selector: 'app-verifier-details-corsia-template',
  templateUrl: './verifier-details-corsia-template.component.html',
  standalone: true,
  imports: [SharedModule, VerificationBodyDetailsInfoTemplateComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsCorsiaTemplateComponent {
  @Input() verificationBodyDetails: VerificationBodyDetails;
  @Input() verifierDetails: AviationAerCorsiaVerifierDetails;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
