import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRRequestMetaData, RequestTaskDTO } from 'pmrv-api';

import { AlrService } from '../core';
import { AlrTaskSharedModule } from '../shared/alr-task-shared.module';
import { resolveVerifierSectionStatus, submitVerificationWizardComplete, taskListTitle } from '../utils';

interface ViewModel {
  pageTitle: string;
  expectedTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  opinionStatementSectionStatus: TaskItemStatus;
  overallDecisionSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectOpinionStatementLink: string;
  redirectOverallDecisionLink: string;
  redirectSendReportLink: string;
}

@Component({
  selector: 'app-verification-submit-task-list',
  templateUrl: './verification-submit-task-list.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationSubmitTaskListComponent {
  daysRemaining = this.alrService.daysRemaining;
  payload = this.alrService.payload;
  requestTaskType = this.alrService.requestTaskType;
  requestMetadata = this.alrService.requestMetadata;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();

    return {
      pageTitle: taskListTitle(requestTaskType, (requestMetadata as ALRRequestMetaData)?.year),
      expectedTaskType: requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './activity',
      redirectOverallDecisionLink: './overall-decision/summary',
      redirectOpinionStatementLink: './opinion-statement/summary',
      redirectSendReportLink: submitVerificationWizardComplete(payload) ? 'send-report-to-operator' : null,
      detailsSectionStatus: resolveVerifierSectionStatus(payload, 'activity'),
      opinionStatementSectionStatus: resolveVerifierSectionStatus(payload, 'opinionStatement'),
      overallDecisionSectionStatus: resolveVerifierSectionStatus(payload, 'overallDecision'),
      sendReportSectionStatus: resolveVerifierSectionStatus(payload, 'sendReport'),
    };
  });

  constructor(private readonly alrService: AlrService) {}
}
