import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { VirApplicationRespondedToRegulatorCommentsRequestActionPayload } from 'pmrv-api';

import { VirService } from '../core/vir.service';

@Component({
  selector: 'app-responded',
  templateUrl: './responded.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RespondedComponent {
  virPayload$ = this.virService.payload$ as Observable<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>;
  reference$ = this.virPayload$.pipe(
    map((payload) => payload?.verifierUncorrectedItem?.reference ?? payload?.verifierComment?.reference),
  );
  virTitle$ = this.reference$.pipe(map((reference) => `Follow up response to ${reference}`));

  verificationDataItem$ = this.virPayload$.pipe(
    map((payload) => payload?.verifierUncorrectedItem ?? payload?.verifierComment),
  );
  operatorImprovementResponse$ = this.virPayload$.pipe(map((payload) => payload?.operatorImprovementResponse));
  regulatorImprovementResponse$ = this.virPayload$.pipe(map((payload) => payload?.regulatorImprovementResponse));
  documentFiles$ = this.operatorImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.virService.getDownloadUrlFiles(payload?.files) : [])),
  );
  operatorImprovementFollowUpResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementFollowUpResponse),
  );

  constructor(private readonly virService: VirService) {}
}
