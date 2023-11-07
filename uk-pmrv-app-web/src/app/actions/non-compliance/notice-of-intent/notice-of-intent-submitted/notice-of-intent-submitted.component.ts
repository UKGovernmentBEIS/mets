import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-notice-of-intent-submitted',
  templateUrl: './notice-of-intent-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoticeOfIntentSubmittedComponent {
  payload$ = (this.nonComplianceService.getPayload() as Observable<NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload>).pipe(
    first(),
    map((payload) => payload),
  );

  documentFiles$ = this.payload$.pipe(
    first(),
    map((payload) => payload?.noticeOfIntent),
    map((file) => (file ? this.nonComplianceService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(readonly nonComplianceService: NonComplianceService) {}
}
