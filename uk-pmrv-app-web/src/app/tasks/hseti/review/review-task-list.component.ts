import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
  HSETIRequestMetadata,
  ItemDTO,
  RequestItemsService,
  RequestTaskDTO,
} from 'pmrv-api';

import { HseTiService } from '../core';
import {
  resolveRegulatorSectionStatus,
  submitRegulatorAllSectionsComplete,
  submitRegulatorWizardComplete,
  taskListTitle,
} from '../utils';
import { getHsetiPreviewDocumentsInfo } from '../utils/previewDocumentsHseti.util';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  overallDecisionSectionStatus: TaskItemStatus;
  redirectOverallDecisionLink: string;
  allowReturnForAmends: boolean;
  allowPeerReviewDecision: boolean;
  allocationPeriod: string;
  allowNotify: boolean;
  allowSendForPeerReview: boolean;
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-hseti-review-task-list',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './review-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTaskListComponent {
  daysRemaining = this.hsetiService.daysRemaining;
  requestTaskType = this.hsetiService.requestTaskType;
  requestMetadata = this.hsetiService.requestMetadata;
  requestTaskItem = this.hsetiService.requestTaskItem;
  allocationPeriod = this.hsetiService?.allocationPeriod as Signal<string>;
  hsetiPayload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;

  baseUrl = computed(() => {
    const payload = this.hsetiPayload();
    switch (payload?.payloadType) {
      case 'HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
        return './';
      case 'HSE_TI_WAIT_FOR_AMENDS_PAYLOAD':
      case 'HSE_TI_APPLICATION_PEER_REVIEW_PAYLOAD':
      case 'HSE_TI_WAIT_FOR_PEER_REVIEW_PAYLOAD':
        return '../review/';

      default:
        return '';
    }
  });

  relatedTasks$: Observable<ItemDTO[]> = this.commonTaskStore.relatedTasksItems$;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.hsetiPayload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();
    const requestTaskItem = this.requestTaskItem();

    return {
      pageTitle: taskListTitle(requestTaskType, (requestMetadata as HSETIRequestMetadata)?.allocationPeriod),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: `${this.baseUrl()}details`,
      detailsSectionStatus: resolveRegulatorSectionStatus(payload, 'HSETI'),
      redirectOverallDecisionLink: submitRegulatorWizardComplete(payload)
        ? this.baseUrl() + 'overall-decision/summary'
        : null,
      overallDecisionSectionStatus: resolveRegulatorSectionStatus(payload, 'OVERALL_DECISION'),
      allowReturnForAmends:
        payload?.regulatorReviewGroupDecisions?.HSETI?.type === 'OPERATOR_AMENDS_NEEDED' &&
        requestTaskItem?.allowedRequestTaskActions.includes('HSE_TI_REGULATOR_REVIEW_RETURN_FOR_AMENDS'),
      allowPeerReviewDecision: requestTaskItem?.allowedRequestTaskActions.includes(
        'HSE_TI_SUBMIT_PEER_REVIEW_DECISION',
      ),
      allocationPeriod: this.allocationPeriod(),
      allowNotify: submitRegulatorAllSectionsComplete(payload),
      allowSendForPeerReview: submitRegulatorAllSectionsComplete(payload),
      previewDocuments:
        requestTaskType === 'HSE_TI_APPLICATION_PEER_REVIEW'
          ? getHsetiPreviewDocumentsInfo(requestTaskType, payload.overallDecision?.type)
          : null,
    };
  });

  constructor(
    private readonly commonTaskStore: CommonTasksStore,
    protected readonly requestItemsService: RequestItemsService,
    private readonly hsetiService: HseTiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendForPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  peerReviewDecision() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}
