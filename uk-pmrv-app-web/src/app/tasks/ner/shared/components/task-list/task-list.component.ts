import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { NerService } from '@tasks/ner/core';
import {
  allowCompleteOrWithdraw,
  allowPeerReviewDecision,
  allowReturnForAmends,
  allowSendForPeerReview,
  nerResolveSectionStatus,
  nerReviewTasks,
  nerSubmitRequestTasks,
  nerTaskListTitle,
  nerVerificationRequestTasks,
  nerWaitTasks,
  nerWarningText,
  nerWizardsCompleted,
} from '@tasks/ner/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectSendReportLink: string;
  isWaitTask: boolean;
  warningText: { text: string; extraText?: string };
  notification: boolean;
  isSubmitRequestTask: boolean;
  isVerificationTask: boolean;
  opinionStatementSectionStatus: TaskItemStatus;
  redirectOpinionStatementLink: string;
  overallDecisionStatus: TaskItemStatus;
  redirectOverallDecisionLink: string;
  isReviewTask: boolean;
  reviewOutcomeStatus: TaskItemStatus;
  redirectReviewOutcomeLink: string;
  allowReturnForAmends: boolean;
  changesRequestedSectionStatus: TaskItemStatus;
  allowSendForPeerReview: boolean;
  allowPeerReviewDecision: boolean;
  allowCompleteOrWithdraw: boolean;
  completeOrWithdrawButtonText: string;
}

@Component({
  selector: 'app-ner-task-list',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './task-list.component.html',
  styles: `
    :host ::ng-deep .app-task-list {
      list-style-type: none;
      padding-left: 0;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerTaskListComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly payload = this.nerService.payload;
  private readonly daysRemaining = this.nerService.daysRemaining;
  private readonly requestTaskType = this.nerService.requestTaskType;
  private readonly requestTaskItem = this.nerService.requestTaskItem;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const isSubmitRequestTask = nerSubmitRequestTasks.includes(requestTaskType);
    const isVerificationTask = nerVerificationRequestTasks.includes(requestTaskType);
    const isReviewTask = nerReviewTasks.includes(requestTaskType);
    const requestTaskItem = this.requestTaskItem();

    return {
      pageTitle: nerTaskListTitle(requestTaskType),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './details',
      detailsSectionStatus: nerResolveSectionStatus(payload, 'NER'),
      redirectSendReportLink: nerWizardsCompleted(payload) ? './send-report' : null,
      sendReportSectionStatus: nerResolveSectionStatus(payload, 'sendReport'),
      isWaitTask: nerWaitTasks.includes(requestTaskType),
      warningText: nerWarningText[requestTaskType],
      notification: this.router.currentNavigation()?.extras.state?.notification,
      isSubmitRequestTask,
      isVerificationTask,
      opinionStatementSectionStatus: nerResolveSectionStatus(payload, 'OPINION_STATEMENT'),
      redirectOpinionStatementLink: './opinion-statement',
      overallDecisionStatus: nerResolveSectionStatus(payload, 'OVERALL_DECISION'),
      redirectOverallDecisionLink: './overall-decision',
      isReviewTask,
      reviewOutcomeStatus: nerResolveSectionStatus(payload, 'OUTCOME'),
      redirectReviewOutcomeLink: nerWizardsCompleted(payload) ? './outcome' : null,
      allowReturnForAmends: allowReturnForAmends(requestTaskItem),
      changesRequestedSectionStatus: nerResolveSectionStatus(payload, 'changesRequested'),
      allowSendForPeerReview: allowSendForPeerReview(requestTaskItem),
      allowPeerReviewDecision: allowPeerReviewDecision(requestTaskItem),
      allowCompleteOrWithdraw: allowCompleteOrWithdraw(requestTaskItem),
      completeOrWithdrawButtonText:
        payload?.regulatorReviewOutcome?.opinion === 'PROCEED_TO_AUTHORITY' ? 'Complete' : 'Withdraw',
    };
  });

  sendReturnForAmends(): void {
    this.router.navigate(['./return-for-amends'], { relativeTo: this.route });
  }

  sendForPeerReview() {
    this.router.navigate(['./send-for-peer-review'], { relativeTo: this.route });
  }

  peerReviewDecision() {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }

  completeOrWithdraw() {
    this.router.navigate(['complete-withdraw'], { relativeTo: this.route });
  }
}
