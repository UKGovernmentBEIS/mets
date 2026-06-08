import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { ReviewDecisionPayload } from '@shared/types';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';
import {
  nerOverallDecisionHeading,
  nerReturnLinkLevelsUp,
  nerReviewTasks,
  nerVerificationDataIsEditable,
} from '@tasks/ner/utils';

import { NERApplicationVerificationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

export interface ViewModel {
  heading: string;
  requestTaskType: RequestTaskDTO['type'];
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
  isReviewTask: boolean;
  isDecisionEditable: boolean;
  payload: ReviewDecisionPayload;
  downloadUrl: string;
  requestTaskId: number;
  returnLinkLevelsUp: number;
}

@Component({
  selector: 'app-ner-overall-decision-summary',
  imports: [SharedModule, NerTaskComponent, ReviewGroupDecisionSharedComponent],
  templateUrl: './overall-decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerOverallDecisionSummaryComponent {
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

    return {
      heading: nerOverallDecisionHeading(requestTaskType),
      requestTaskType,
      isEditable,
      hideSubmit: payload.verificationSectionsCompleted?.['OVERALL_DECISION']?.[0] || !isEditable,
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
      isDecisionEditable: this.nerService.isDecisionComponentEditable(),
      isReviewTask: nerReviewTasks.includes(requestTaskType),
      payload: payload as ReviewDecisionPayload,
      downloadUrl: this.nerService.getBaseFileDownloadUrl(),
      requestTaskId: this.requestTaskId,
      returnLinkLevelsUp: nerReturnLinkLevelsUp(requestTaskType),
    };
  });

  onConfirm() {
    this.nerService
      .postVerificationTaskSave(null, true, 'OVERALL_DECISION')
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
