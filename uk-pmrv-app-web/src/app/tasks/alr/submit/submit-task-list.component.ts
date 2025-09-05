import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { AlrService } from '@tasks/alr/core';
import {
  resolveSectionStatus,
  submitWizardComplete,
  taskListTitle,
  waitTasksAlr,
  warningTextAlr,
} from '@tasks/alr/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRRequestMetaData, RequestTaskDTO } from 'pmrv-api';

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
}

@Component({
  selector: 'app-alr-submit-task-list',
  standalone: true,
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './submit-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrTaskListComponent {
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
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './activity/summary',
      redirectSendReportLink: './send-report',
      detailsSectionStatus: resolveSectionStatus(payload, 'activity'),
      sendReportSectionStatus: resolveSectionStatus(payload, 'sendReport'),
      changesRequestedSectionStatus: resolveSectionStatus(payload, 'changesRequested'),
      isWaitTask: waitTasksAlr.includes(requestTaskType),
      warningText: warningTextAlr[requestTaskType],
      sectionsCompleted: submitWizardComplete(payload),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
    };
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
  ) {}
}
