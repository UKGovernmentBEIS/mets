import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import {
  ALRAlrDataRegulatorReviewDecision,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRRequestMetaData,
  RequestTaskDTO,
} from 'pmrv-api';

import { AlrService } from '../core';
import {
  allSectionsReviewComplete,
  resolveRegulatorSectionStatus,
  submitRegulatorWizardComplete,
  taskListTitle,
  waitTasksAlr,
  warningTextAlr,
} from '../utils';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  opinionStatementSectionStatus: TaskItemStatus;
  redirectOpinionStatementLink: string;
  overallDecisionSectionStatus: TaskItemStatus;
  redirectOverallDecisionLink: string;
  informationSectionStatus: TaskItemStatus;
  redirectAlcLink: string;
  determinationSectionStatus: TaskItemStatus;
  redirectDeterminationLink: string;
  allowReturnForAmends: boolean;
  isWaitTask: boolean;
  warningText: string;
  allowCompleteReview: boolean;
  allowNotify: boolean;
  allowSendForPeerReview: boolean;
  allowPeerReviewDecision: boolean;
}

@Component({
  selector: 'app-alr-review-task-list',
  standalone: true,
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './review-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReviewTaskListComponent {
  daysRemaining = this.alrService.daysRemaining;
  requestTaskType = this.alrService.requestTaskType;
  requestMetadata = this.alrService.requestMetadata;
  requestTaskItem = this.alrService.requestTaskItem;
  alrPayload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  baseUrl = computed(() => {
    const payload = this.alrPayload();
    switch (payload?.payloadType) {
      case 'ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
        return './';

      default:
        return '';
    }
  });

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.alrPayload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata() as ALRRequestMetaData;
    const requestTaskItem = this.requestTaskItem();

    return {
      pageTitle: taskListTitle(requestTaskType, requestMetadata?.year, requestMetadata?.isFinal),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: `${this.baseUrl()}activity`,
      detailsSectionStatus: resolveRegulatorSectionStatus(payload, 'ALR'),
      redirectOverallDecisionLink: this.baseUrl() + 'overall-decision',
      overallDecisionSectionStatus: resolveRegulatorSectionStatus(payload, 'OVERALL_DECISION'),
      redirectOpinionStatementLink: this.baseUrl() + 'opinion-statement',
      opinionStatementSectionStatus: resolveRegulatorSectionStatus(payload, 'OPINION_STATEMENT'),
      redirectAlcLink: `${this.baseUrl()}alc-information/summary`,
      informationSectionStatus: resolveRegulatorSectionStatus(payload, 'ALC'),
      determinationSectionStatus: resolveRegulatorSectionStatus(payload, 'DETERMINATION'),
      redirectDeterminationLink: submitRegulatorWizardComplete(payload) ? this.baseUrl() + 'determination' : null,
      allowReturnForAmends:
        (payload?.regulatorReviewGroupDecisions?.ALR as ALRAlrDataRegulatorReviewDecision)?.type ===
          'OPERATOR_AMENDS_NEEDED' &&
        requestTaskItem.allowedRequestTaskActions.includes('ALR_REGULATOR_REVIEW_RETURN_FOR_AMENDS'),
      isWaitTask: waitTasksAlr.includes(requestTaskType),
      warningText: warningTextAlr[requestTaskType],
      allowCompleteReview:
        allSectionsReviewComplete(payload) &&
        (payload?.regulatorReviewOutcome.determination.type === 'CLOSED_ALR' ||
          !payload?.regulatorReviewOutcome.determination?.['needsOfficialNotice']),
      allowNotify:
        allSectionsReviewComplete(payload) &&
        payload?.regulatorReviewOutcome.determination.type === 'PROCEED_TO_AUTHORITY' &&
        payload?.regulatorReviewOutcome.determination?.['needsOfficialNotice'],
      allowSendForPeerReview: allSectionsReviewComplete(payload),
      allowPeerReviewDecision: requestTaskItem.allowedRequestTaskActions.includes('ALR_SUBMIT_PEER_REVIEW_DECISION'),
    };
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  completeReview(): void {
    this.router.navigate(['complete-task'], { relativeTo: this.route });
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
