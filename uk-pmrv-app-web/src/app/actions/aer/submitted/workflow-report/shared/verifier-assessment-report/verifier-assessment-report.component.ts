import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../../../core/aer.service';

@Component({
  selector: 'app-verifier-assessment-report',
  templateUrl: './verifier-assessment-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierAssessmentReportComponent {
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();

  activityLevelReportFiles$ = this.payload$.pipe(
    map((payload) => payload.verificationReport?.activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file], true) : [])),
  );
  constructor(readonly aerService: AerService) {}
}
