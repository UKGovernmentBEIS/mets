import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { AlrService } from '@tasks/alr/core';
import { resolveSectionStatus, taskListTitle, waitTasksAlr, warningTextAlr } from '@tasks/alr/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRRequestMetaData, RequestTaskDTO } from 'pmrv-api';

import { AlrTaskSharedModule } from '../shared/alr-task-shared.module';

interface ViewModel {
  pageTitle: string;
  expectedTaskType: RequestTaskDTO['type'];
  daysRemaining: number;
  detailsSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectSendReportLink: string;
  isWaitTask: boolean;
  warningText: string;
}

@Component({
  selector: 'app-alr-task-list',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
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
      expectedTaskType: requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './activity/summary',
      redirectSendReportLink: './send-report-verifier',
      detailsSectionStatus: resolveSectionStatus(payload, 'activity'),
      sendReportSectionStatus: resolveSectionStatus(payload, 'sendReport'),
      isWaitTask: waitTasksAlr.includes(requestTaskType),
      warningText: warningTextAlr[requestTaskType],
    };
  });

  constructor(
    private readonly alrService: AlrService,
    readonly route: ActivatedRoute,
  ) {}
}
