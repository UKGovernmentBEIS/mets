import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-daily-penalty-notice-submitted',
  templateUrl: './daily-penalty-notice-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DailyPenaltyNoticeSubmittedComponent {
  payload$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload),
  );

  documentFiles$ = this.payload$.pipe(
    first(),
    map((payload) => payload?.dailyPenaltyNotice),
    map((file) => (file ? this.nonComplianceService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(readonly nonComplianceService: NonComplianceService) {}
}
