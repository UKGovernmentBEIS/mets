import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { BdrS2VerificationReviewGroupDecisionComponent } from '@tasks/bdrs2/shared/components/decision/bdrs2-review-decision/bdrs2-verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, BdrS2VerificationReviewGroupDecisionComponent],
  templateUrl: './opinion-statement-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2OpinionStatementReviewComponent {
  readonly isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  readonly bdrs2Payload: Signal<BDRS2ApplicationVerificationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  readonly opinionStatementFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.bdrs2Payload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFiles
      ? this.bdrs2Service.getVerifierDownloadUrlFiles(
          payload?.verificationReport?.opinionStatement?.opinionStatementFiles,
        )
      : [];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
  ) {}
}
