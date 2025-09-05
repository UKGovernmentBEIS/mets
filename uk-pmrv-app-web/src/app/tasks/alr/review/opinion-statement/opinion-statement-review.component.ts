import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { AlrVerificationReviewGroupDecisionComponent } from '@tasks/alr/shared/components/decision/alr-verification-review-group-decision/alr-verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-verification-opinion-statement-review',
  templateUrl: './opinion-statement-review.component.html',
  standalone: true,
  imports: [
    SharedModule,
    TaskSharedModule,
    AlrTaskSharedModule,
    ActivitySummaryTemplateComponent,
    AlrVerificationReviewGroupDecisionComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOpinionStatementReviewComponent {
  isEditable: Signal<boolean> = this.alrService.isEditable;
  alrPayload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

  opinionStatementFiles: Signal<AttachedFile[]> = computed(() => {
    const payload = this.alrPayload();

    return payload?.verificationReport?.opinionStatement?.opinionStatementFiles
      ? this.alrService.getVerifierDownloadUrlFiles(
          payload?.verificationReport?.opinionStatement?.opinionStatementFiles,
        )
      : [];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
  ) {}
}
