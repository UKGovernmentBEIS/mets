import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { RequestTaskDTO } from 'pmrv-api';

import { PermanentCessationService } from '../shared/services/permanent-cessation.service';
import { getPermanentCessationPreviewDocumentsInfo } from '../utils';
import { detailsWizardCompleted, resolveSectionStatus } from './section.status';
import { permanentCessationExpectedTaskType, permanentCessationHeader, permanentCessationWaitTasks } from './submit';

interface ViewModel {
  header: string;
  sectionStatus: TaskItemStatus;
  allowNotifyOperator: boolean;
  allowSendForPeerReview: boolean;
  allowStartPeerReview: boolean;
  expectedTaskType: RequestTaskDTO['type'];
  isWaitTask: boolean;
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  payload = this.permanentCessationService.payload;
  requestTaskType = this.permanentCessationService.requestTaskType;
  allowedRequestTaskActions = this.permanentCessationService.allowedRequestTaskActions;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const wizardCompleted = detailsWizardCompleted(payload.permanentCessation);
    const sectionCompleted = payload.permanentCessationSectionsCompleted.details;
    const allowedRequestTaskActions = this.allowedRequestTaskActions();
    const requestTaskType = this.requestTaskType();

    return {
      header: permanentCessationHeader[requestTaskType],
      sectionStatus: resolveSectionStatus(payload),
      allowNotifyOperator:
        wizardCompleted &&
        sectionCompleted &&
        allowedRequestTaskActions.includes('PERMANENT_CESSATION_SAVE_APPLICATION'),
      allowSendForPeerReview:
        wizardCompleted &&
        sectionCompleted &&
        allowedRequestTaskActions.includes('PERMANENT_CESSATION_REQUEST_PEER_REVIEW'),
      allowStartPeerReview:
        wizardCompleted &&
        sectionCompleted &&
        allowedRequestTaskActions.includes('PERMANENT_CESSATION_SUBMIT_PEER_REVIEW_DECISION'),
      expectedTaskType: permanentCessationExpectedTaskType.find((type) => type === requestTaskType),
      isWaitTask: permanentCessationWaitTasks.includes(requestTaskType),
      previewDocuments:
        requestTaskType === 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW' &&
        getPermanentCessationPreviewDocumentsInfo(requestTaskType),
    };
  });

  constructor(
    private readonly permanentCessationService: PermanentCessationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  notifyOperator() {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendForPeerReview() {
    this.router.navigate(['send-for-peer-review'], { relativeTo: this.route });
  }

  startPeerReview() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}
