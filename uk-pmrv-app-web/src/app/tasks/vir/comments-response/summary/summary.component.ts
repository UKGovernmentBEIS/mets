import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { matchVerificationItem } from '@shared/vir-shared/utils/match-verification-item';
import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  reference = this.route.snapshot.data.reference;
  virPayload$ = this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>;
  verificationDataItem$ = this.virPayload$.pipe(
    map((payload) => matchVerificationItem(this.reference, payload?.verificationData)),
  );
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  regulatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.regulatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virPayload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );
  operatorImprovementFollowUpResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementFollowUpResponses?.[this.reference]),
  );
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) => {
          return this.virService.postVirRespondTaskSave({
            reference: this.reference,
            operatorImprovementFollowUpResponse: payload?.operatorImprovementFollowUpResponses?.[this.reference],
            virRespondToRegulatorCommentsSectionsCompleted: {
              ...payload?.virRespondToRegulatorCommentsSectionsCompleted,
              [this.reference]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
