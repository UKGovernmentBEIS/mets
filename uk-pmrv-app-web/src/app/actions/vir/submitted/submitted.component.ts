import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../core/vir.service';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  virPayload$ = this.virService.payload$ as Observable<VirApplicationSubmittedRequestActionPayload>;
  virTitle$ = this.virPayload$.pipe(map((payload) => payload.reportingYear + ' verifier improvement report submitted'));
  verificationDataGroup$ = this.virPayload$.pipe(
    map(
      (payload) =>
        [
          payload?.verificationData?.uncorrectedNonConformities,
          payload?.verificationData?.recommendedImprovements,
          payload?.verificationData?.priorYearIssues,
        ] as { [key: string]: VerificationDataItem }[],
    ),
  );
  constructor(private readonly virService: VirService) {}
}
