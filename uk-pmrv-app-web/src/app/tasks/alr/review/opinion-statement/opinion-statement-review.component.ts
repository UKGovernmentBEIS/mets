import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { AlrVerificationReviewGroupDecisionComponent } from '@tasks/alr/shared/components/decision/alr-verification-review-group-decision/alr-verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, AlrVerificationReviewGroupDecisionComponent],
  templateUrl: './opinion-statement-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOpinionStatementReviewComponent {
  readonly isEditable: Signal<boolean> = this.alrService.isEditable;
  readonly alrPayload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

  readonly opinionStatementFile: Signal<AttachedFile> = computed(() => {
    const payload = this.alrPayload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFile
      ? this.alrService.getVerifierDownloadUrlFile(payload?.verificationReport?.opinionStatement?.opinionStatementFile)
      : ({} as AttachedFile);
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
  ) {}
}
