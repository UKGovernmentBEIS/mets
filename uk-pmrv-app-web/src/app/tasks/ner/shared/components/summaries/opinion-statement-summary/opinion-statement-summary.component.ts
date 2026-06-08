import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { ReviewDecisionPayload } from '@shared/types';
import { AttachedFile } from '@shared/types/attached-file.type';
import { NerService } from '@tasks/ner/core/ner.service';
import { NerTaskComponent } from '@tasks/ner/shared';
import {
  nerOpinionStatementHeading,
  nerReturnLinkLevelsUp,
  nerReviewTasks,
  nerVerificationDataIsEditable,
} from '@tasks/ner/utils';

import {
  NERApplicationVerificationSubmitRequestTaskPayload,
  NERVerificationOpinionStatement,
  RequestTaskDTO,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  isEditable: boolean;
  isDecisionEditable: boolean;
  requestTaskType: RequestTaskDTO['type'];
  opinionStatementFile: AttachedFile;
  supportingFiles: AttachedFile[];
  notes: NERVerificationOpinionStatement['notes'];
  hideSubmit: boolean;
  isReviewTask: boolean;
  payload: ReviewDecisionPayload;
  downloadUrl: string;
  requestTaskId: number;
  returnLinkLevelsUp: number;
}

@Component({
  selector: 'app-ner-opinion-statement-summary',
  imports: [SharedModule, NerTaskComponent, ReviewGroupDecisionSharedComponent],
  templateUrl: './opinion-statement-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerOpinionStatementSummaryComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly isEditable = this.nerService.isEditable;
  private readonly requestTaskType = this.nerService.requestTaskType;
  private readonly payload = this.nerService.payload as Signal<NERApplicationVerificationSubmitRequestTaskPayload>;
  private readonly requestTaskId = this.nerService.requestTaskId;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskType = this.requestTaskType();
    const isEditable = nerVerificationDataIsEditable(requestTaskType, this.isEditable());
    const payload = this.payload();
    const {
      verificationSectionsCompleted,
      verificationReport: { opinionStatement: { opinionStatementFile, supportingFiles, notes } = {} } = {},
    } = payload;

    return {
      heading: nerOpinionStatementHeading(requestTaskType),
      isEditable,
      isDecisionEditable: this.nerService.isDecisionComponentEditable(),
      requestTaskType,
      opinionStatementFile: this.nerService.getVerifierDownloadUrlFile(opinionStatementFile),
      supportingFiles: this.nerService.getVerifierDownloadUrlFiles(supportingFiles),
      notes,
      hideSubmit: verificationSectionsCompleted?.['OPINION_STATEMENT']?.[0] || !isEditable,
      isReviewTask: nerReviewTasks.includes(requestTaskType),
      payload: payload as ReviewDecisionPayload,
      downloadUrl: this.nerService.getBaseFileDownloadUrl(),
      requestTaskId: this.requestTaskId,
      returnLinkLevelsUp: nerReturnLinkLevelsUp(requestTaskType),
    };
  });

  onConfirm() {
    this.nerService
      .postVerificationTaskSave(null, true, 'OPINION_STATEMENT')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  onDecisionSubmit(form: UntypedFormGroup) {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.nerService.postGroupReviewDecision(
            constructReviewDecision(form),
            'VERIFICATION_REPORT_DATA',
            data.groupKey,
            'NER_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
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
