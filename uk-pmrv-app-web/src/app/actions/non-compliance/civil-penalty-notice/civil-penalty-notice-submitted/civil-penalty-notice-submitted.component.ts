import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-civil-penalty-notice-submitted',
  templateUrl: './civil-penalty-notice-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CivilPenaltyNoticeSubmittedComponent {
  payload$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload>
  ).pipe(
    first(),
    map((payload) => payload),
  );

  documentFiles$ = this.payload$.pipe(
    first(),
    map((payload) => payload?.civilPenalty),
    map((file) => (file ? this.nonComplianceService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(readonly nonComplianceService: NonComplianceService) {}
}
