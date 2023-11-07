import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  heading = `Respond to ${this.reference}`;
  virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>;
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  regulatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.regulatorReviewResponse?.regulatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virPayload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    (this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>)
      .pipe(
        first(),
        switchMap((payload) => {
          return this.virService.postVirReviewTaskSave({
            regulatorReviewResponse: payload?.regulatorReviewResponse,
            reviewSectionsCompleted: {
              ...payload?.reviewSectionsCompleted,
              [this.reference]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
