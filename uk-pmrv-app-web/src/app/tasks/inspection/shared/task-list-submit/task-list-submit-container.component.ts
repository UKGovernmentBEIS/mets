import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { RequestTaskDTO } from 'pmrv-api';

import { isInstallationInspectionFollowUpSubmitCompleted } from '../../submit/submit.wizard';
import { InspectionSubmitRequestTaskPayload, peerReviewActionTypeMap } from '../inspection';
import { DetailsSubtaskHeaderPipe } from '../pipes/details-subtask-header.pipe';
import { DetailsSubtaskLinktextPipe } from '../pipes/details-subtask-linktext.pipe';
import { getInspectionPreviewDocumentsInfo } from '../previewDocumentsInspection.util';
import {
  detailsStatusKeySubmit,
  followUpStatusKeySubmit,
  resolveDetailsSectionStatusSubmit,
  resolveSectionStatusSubmit,
} from '../section-status';
import { isWaitForPeerReviewTask, notifyOperatorMap, peerReviewDecisionMap, sendPeerReviewMap } from './task-list';

interface ViewModel {
  pageTitle: string;
  sectionStatus: TaskItemStatus;
  reportSectionStatus: TaskItemStatus;
  allowSendPeerReview: boolean;
  allowNotifyOperator: boolean;
  allowPeerReviewDecision: boolean;
  redirectFollowUpLink: string;
  redirectReportLink: string;
  expectedTaskType: RequestTaskDTO['type'];
  isWaitForPeerReviewTask: boolean;
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-task-list-submit-container',
  templateUrl: './task-list-submit-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, DetailsSubtaskHeaderPipe, DetailsSubtaskLinktextPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskListSubmitContainerComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.route.data,
    this.inspectionService.payload$ as Observable<InspectionSubmitRequestTaskPayload>,
    this.inspectionService.isEditable$,
    this.inspectionService.requestTaskItem$,
  ]).pipe(
    map(([data, payload, isEditable, requestTaskItem]) => {
      const taskType = requestTaskItem?.requestTask.type;

      const followUpSectionStatus = resolveSectionStatusSubmit(payload, followUpStatusKeySubmit);

      const reportSectionStatus = resolveDetailsSectionStatusSubmit(payload, taskType, detailsStatusKeySubmit);

      const canViewFollowUpSectionDetails = isEditable || followUpSectionStatus !== 'not started';
      const canViewReportSectionDetails = isEditable || reportSectionStatus !== 'not started';
      const followUpActionCompleted = payload?.installationInspectionSectionsCompleted?.followUpAction;
      const reportActionCompleted = payload?.installationInspectionSectionsCompleted?.details;

      return {
        pageTitle: data.pageTitle,
        sectionStatus: followUpSectionStatus,
        reportSectionStatus,
        allowSendPeerReview:
          canViewFollowUpSectionDetails &&
          canViewReportSectionDetails &&
          requestTaskItem?.allowedRequestTaskActions.includes(sendPeerReviewMap[taskType]) &&
          followUpActionCompleted &&
          reportActionCompleted &&
          followUpSectionStatus !== 'needs review',
        allowNotifyOperator:
          canViewFollowUpSectionDetails &&
          canViewReportSectionDetails &&
          requestTaskItem?.allowedRequestTaskActions.includes(notifyOperatorMap[taskType]) &&
          followUpActionCompleted &&
          reportActionCompleted &&
          followUpSectionStatus !== 'needs review',
        allowPeerReviewDecision:
          canViewFollowUpSectionDetails &&
          canViewReportSectionDetails &&
          requestTaskItem?.allowedRequestTaskActions.includes(peerReviewDecisionMap[taskType]) &&
          followUpActionCompleted &&
          reportActionCompleted,
        redirectFollowUpLink: canViewFollowUpSectionDetails
          ? isInstallationInspectionFollowUpSubmitCompleted(payload?.installationInspection)
            ? './follow-up-summary'
            : payload?.installationInspection?.followUpActions?.length &&
                payload?.installationInspection?.followUpActionsRequired === true
              ? './follow-up-actions'
              : 'follow-up-actions-guard-question'
          : null,
        redirectReportLink: canViewReportSectionDetails ? './details-summary' : null,
        expectedTaskType: taskType,
        isWaitForPeerReviewTask: isWaitForPeerReviewTask.includes(taskType),
        previewDocuments: getInspectionPreviewDocumentsInfo(peerReviewActionTypeMap[taskType]),
      };
    }),
  );

  constructor(
    public readonly inspectionService: InspectionService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  sendPeerReview() {
    this.router.navigate(['./peer-review'], { relativeTo: this.route });
  }

  notifyOperator() {
    this.router.navigate(['./notify-operator'], { relativeTo: this.route });
  }
  peerReviewDecision() {
    this.router.navigate(['./decision'], { relativeTo: this.route });
  }
}
