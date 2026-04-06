import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { NerService } from '@tasks/ner/core';
import {
  nerResolveSectionStatus,
  nerSubmitRequestTasks,
  nerSubmitWizardComplete,
  nerTaskListTitle,
  nerWaitTasks,
  nerWarningText,
} from '@tasks/ner/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import {
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

interface ViewModel {
  pageTitle: string;
  requestTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectSendReportLink: string;
  isWaitTask: boolean;
  warningText: string;
  sectionsCompleted: boolean;
  notification: boolean;
  isSubmitRequestTask: boolean;
  hasVerificationReport: boolean;
  opinionStatementSectionStatus: TaskItemStatus;
  redirectOpinionStatementLink: string;
  overallDecisionStatus: TaskItemStatus;
  redirectOverallDecisionLink: string;
}

@Component({
  selector: 'app-ner-task-list',
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerTaskListComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly payload = this.nerService.payload as Signal<NerApplicationSubmitRequestTaskPayload>;
  private readonly daysRemaining = this.nerService.daysRemaining;
  private readonly requestTaskType = this.nerService.requestTaskType;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const isSubmitRequestTask = nerSubmitRequestTasks.includes(requestTaskType);
    const hasVerificationReport = !!(payload as NERApplicationVerificationSubmitRequestTaskPayload).verificationReport;
    const opinionStatementSectionStatus = hasVerificationReport
      ? nerResolveSectionStatus(payload, 'verificationReport')
      : null;
    const overallDecisionStatus = hasVerificationReport ? nerResolveSectionStatus(payload, 'overallDecision') : null;

    return {
      pageTitle: nerTaskListTitle(requestTaskType),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './details',
      detailsSectionStatus: nerResolveSectionStatus(payload, 'details'),
      redirectSendReportLink: './send-report',
      sendReportSectionStatus: nerResolveSectionStatus(payload, 'sendReport'),
      isWaitTask: nerWaitTasks.includes(requestTaskType),
      warningText: nerWarningText[requestTaskType],
      notification: this.router.currentNavigation()?.extras.state?.notification,
      isSubmitRequestTask,
      sectionsCompleted: nerSubmitWizardComplete(payload),
      hasVerificationReport,
      opinionStatementSectionStatus,
      redirectOpinionStatementLink: './',
      overallDecisionStatus,
      redirectOverallDecisionLink: './',
    };
  });
}
