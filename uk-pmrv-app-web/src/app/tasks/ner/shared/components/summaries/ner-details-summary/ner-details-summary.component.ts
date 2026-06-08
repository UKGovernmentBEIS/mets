import { ChangeDetectionStrategy, Component, computed, inject, OnInit, Signal, signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { NerDetailsSummaryTemplateComponent } from '@shared/components/ner';
import { ReviewGroupDecisionSharedComponent } from '@shared/components/review-group-decision/review-group-decision.component';
import { constructReviewDecision } from '@shared/components/review-group-decision/review-group-decision.utils';
import { SharedModule } from '@shared/shared.module';
import { ReviewDecisionPayload } from '@shared/types';
import { AttachedFile } from '@shared/types/attached-file.type';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';
import {
  nerDetailsCaption,
  nerDetailsDataIsEditable,
  nerDetailsHeading,
  nerReturnLinkLevelsUp,
  nerReviewTasks,
} from '@tasks/ner/utils';

import { NER, NerApplicationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

import { NerReviewOutcomeComponent } from './review-outcome/review-outcome.component';

interface ViewModel {
  heading: string;
  caption: string;
  isReviewTask: boolean;
  returnLinkLevelsUp: number;
  isEditable: boolean;
  isDecisionEditable: boolean;
  showDecision: boolean;
  hideSubmit: boolean;
  requestTaskType: RequestTaskDTO['type'];
  ner: NER;
  nerFile: AttachedFile;
  nerSupportingFiles: AttachedFile[];
  mmpFile: AttachedFile;
  mmpSupportingFiles: AttachedFile[];
  payload: ReviewDecisionPayload;
  downloadUrl: string;
  requestTaskId: number;
  isOutcomeSubtask: boolean;
  enableSummary: boolean;
}

@Component({
  selector: 'app-ner-details-summary',
  imports: [
    SharedModule,
    NerTaskComponent,
    NerDetailsSummaryTemplateComponent,
    ReviewGroupDecisionSharedComponent,
    NerReviewOutcomeComponent,
  ],
  templateUrl: './ner-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerDetailsSummaryComponent implements OnInit {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload as Signal<NerApplicationSubmitRequestTaskPayload>;
  private readonly requestTaskType = this.nerService.requestTaskType;
  private readonly isEditable = this.nerService.isEditable;
  private readonly requestTaskId = this.nerService.requestTaskId;
  private readonly enableSummary = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const ner = payload.ner;
    const requestTaskType = this.requestTaskType();
    const isEditable = this.isEditable() && nerDetailsDataIsEditable(requestTaskType, this.isEditable());
    const hideSubmit = !isEditable || payload.nerSectionsCompleted['NER'];
    const isOutcomeSubtask = this.router.url.split('/').includes('outcome');
    const isReviewTask = nerReviewTasks.includes(requestTaskType);

    return {
      heading: nerDetailsHeading(requestTaskType, isOutcomeSubtask),
      caption: nerDetailsCaption(requestTaskType, isOutcomeSubtask),
      isReviewTask,
      returnLinkLevelsUp: nerReturnLinkLevelsUp(requestTaskType, isOutcomeSubtask ? 'OUTCOME' : 'NER'),
      isEditable,
      isDecisionEditable: this.nerService.isDecisionComponentEditable(),
      showDecision: !isOutcomeSubtask && isReviewTask,
      hideSubmit,
      requestTaskType,
      ner,
      nerFile: this.nerService.getOperatorDownloadUrlFile(ner.nerFiles.file),
      nerSupportingFiles: this.nerService.getOperatorDownloadUrlFiles(ner.nerFiles.supportingFiles),
      mmpFile: this.nerService.getOperatorDownloadUrlFile(ner.mmpFiles.file),
      mmpSupportingFiles: this.nerService.getOperatorDownloadUrlFiles(ner.mmpFiles.supportingFiles),
      payload: payload as ReviewDecisionPayload,
      downloadUrl: this.nerService.getBaseFileDownloadUrl(),
      requestTaskId: this.requestTaskId,
      isOutcomeSubtask,
      enableSummary: this.enableSummary(),
    };
  });

  ngOnInit(): void {
    this.enableSummary.set(this.route.snapshot.data['isSummary'] ?? false);
  }

  onSubmit() {
    this.nerService
      .postTaskSave({}, {}, true, 'NER')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }

  onDecisionSubmit(form: UntypedFormGroup) {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.nerService.postGroupReviewDecision(
            constructReviewDecision(form),
            'NER_DATA',
            data.groupKey,
            'NER_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
            form.controls.requiredChanges.value.map((requiredChange: any) => requiredChange.files).flat(),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
