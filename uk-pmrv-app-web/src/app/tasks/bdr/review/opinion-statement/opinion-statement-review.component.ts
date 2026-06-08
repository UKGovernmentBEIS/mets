import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, ReviewGroupDecisionSharedComponent],
  templateUrl: './opinion-statement-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOpinionStatementReviewComponent {
  readonly isEditable: Signal<boolean> = this.bdrService.isEditable;
  readonly bdrPayload: Signal<BDRApplicationVerificationSubmitRequestTaskPayload> = this.bdrService.payload;
  readonly requestTaskId = this.bdrService.requestTaskId;
  readonly opinionStatementFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrPayload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFiles
      ? this.bdrService.getVerifierDownloadUrlFiles(
          payload?.verificationReport?.opinionStatement?.opinionStatementFiles,
        )
      : [];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(form: UntypedFormGroup): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.bdrService.postGroupDecisionReview(
            constructReviewDecision(form),
            'VERIFICATION_REPORT_DATA',
            data.groupKey,
            [],
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
