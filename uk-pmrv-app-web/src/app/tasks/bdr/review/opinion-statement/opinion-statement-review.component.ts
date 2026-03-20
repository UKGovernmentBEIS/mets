import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrVerificationReviewGroupDecisionComponent } from '@tasks/bdr/shared/components/decision/bdr-review-decision/bdr-verification-review-group-decision.component';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, BdrVerificationReviewGroupDecisionComponent],
  templateUrl: './opinion-statement-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOpinionStatementReviewComponent {
  readonly isEditable: Signal<boolean> = this.bdrService.isEditable;
  readonly bdrPayload: Signal<BDRApplicationVerificationSubmitRequestTaskPayload> = this.bdrService.payload;

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
  ) {}
}
