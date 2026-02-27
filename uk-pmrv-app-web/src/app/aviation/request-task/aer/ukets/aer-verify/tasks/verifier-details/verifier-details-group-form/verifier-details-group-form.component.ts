import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-aer-verifier-details-group-form',
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './verifier-details-group-form.component.html',
})
export default class AerVerifierDetailsGroupFormComponent {
  @Input() isEditable = false;
  @Input() showVerifierDetails = true;
  @Input() verificationReport: AviationAerUkEtsVerificationReport;
  @Input() changeUrlQueryParams: Params = {};
}
