import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Observable } from 'rxjs';

import { AerApplicationVerificationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../../../core/aer.service';

@Component({
  selector: 'app-verifier-findings-report',
  templateUrl: './verifier-findings-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierFindingsReportComponent {
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();

  constructor(readonly aerService: AerService) {}
}
