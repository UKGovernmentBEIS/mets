import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import {
  amendsSubmittedTasks,
  resolveRegulatorSectionStatus,
  submitRegulatorWizardComplete,
  submitRequestTasks,
  wasteQdrResolveSectionStatus,
  wasteQdrSubmitWizardComplete,
  wasteQdrTaskListTitle,
  wasteQdrWaitTasks,
  wasteQdrWarningText,
} from '@tasks/waste-qdr/utils';

import {
  RequestTaskDTO,
  WasteQDRApplicationSubmitRequestTaskPayload,
  WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'pmrv-api';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  changesRequestedSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectSendReportLink: string;
  isWaitTask: boolean;
  warningText: string;
  sectionsCompleted: boolean;
  notification: boolean;
  allowCompleteReview: boolean;
  isSubmitRequestTask: boolean;
  allowReturnForAmends: boolean;
  isAmendsSubmitted: boolean;
}

@Component({
  selector: 'app-waste-qdr-task-list',
  standalone: true,
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrTaskListComponent {
  daysRemaining = this.wasteQdrService.daysRemaining;
  payload = this.wasteQdrService.payload as Signal<
    WasteQDRApplicationSubmitRequestTaskPayload & WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload
  >;
  requestTaskType = this.wasteQdrService.requestTaskType;
  requestMetadata = this.wasteQdrService.requestMetadata;
  requestTaskItem = this.wasteQdrService.requestTaskItem;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();
    const requestTaskItem = this.requestTaskItem();
    const isSubmitRequestTask = submitRequestTasks.includes(requestTaskType);

    return {
      pageTitle: wasteQdrTaskListTitle(requestTaskType, requestMetadata?.year, requestMetadata?.quarter),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './qdr',
      redirectSendReportLink: './send-report',
      detailsSectionStatus: isSubmitRequestTask
        ? wasteQdrResolveSectionStatus(payload, 'qdr')
        : resolveRegulatorSectionStatus(payload, 'qdr'),
      sendReportSectionStatus: wasteQdrResolveSectionStatus(payload, 'sendReport'),
      changesRequestedSectionStatus: wasteQdrResolveSectionStatus(payload, 'changesRequested'),
      isWaitTask: wasteQdrWaitTasks.includes(requestTaskType),
      warningText: wasteQdrWarningText[requestTaskType],
      sectionsCompleted: wasteQdrSubmitWizardComplete(payload),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
      allowCompleteReview: submitRegulatorWizardComplete(payload),
      isSubmitRequestTask,
      allowReturnForAmends:
        payload?.reviewDecision?.type === 'OPERATOR_AMENDS_NEEDED' &&
        requestTaskItem.allowedRequestTaskActions.includes('WASTE_QDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS'),
      isAmendsSubmitted: amendsSubmittedTasks.includes(requestTaskType),
    };
  });

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  completeReview(): void {
    this.router.navigate(['complete-task'], { relativeTo: this.route });
  }

  sendReturnForAmends() {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }
}
