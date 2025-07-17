import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrVerificationReviewGroupDecisionComponent } from '@tasks/bdr/shared/components/decision/bdr-review-decision/bdr-verification-review-group-decision.component';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdr-verification-opinion-statement-review',
  templateUrl: './opinion-statement-review.component.html',
  standalone: true,
  imports: [
    SharedModule,
    TaskSharedModule,
    BdrTaskSharedModule,
    BaselineSummaryTemplateComponent,
    BdrVerificationReviewGroupDecisionComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOpinionStatementReviewComponent {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  bdrPayload: Signal<BDRApplicationVerificationSubmitRequestTaskPayload> = this.bdrService.payload;

  opinionStatementFiles: Signal<AttachedFile[]> = computed(() => {
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
