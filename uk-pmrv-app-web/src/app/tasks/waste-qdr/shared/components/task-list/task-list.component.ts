import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import {
  wasteQdrResolveSectionStatus,
  wasteQdrSubmitWizardComplete,
  wasteQdrTaskListTitle,
  wasteQdrWaitTasks,
  wasteQdrWarningText,
} from '@tasks/waste-qdr/utils';

import { RequestTaskDTO } from 'pmrv-api';

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
  selector: 'app-waste-qdr-task-list',
  standalone: true,
  imports: [SharedModule, TaskSharedModule],
  templateUrl: './task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrTaskListComponent {
  daysRemaining = this.wasteQdrService.daysRemaining;
  payload = this.wasteQdrService.payload;
  requestTaskType = this.wasteQdrService.requestTaskType;
  requestMetadata = this.wasteQdrService.requestMetadata;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();

    return {
      pageTitle: wasteQdrTaskListTitle(requestTaskType, requestMetadata?.year, requestMetadata?.quarter),
      requestTaskType,
      daysRemaining: this.daysRemaining(),
      redirectDetailsLink: './qdr',
      redirectSendReportLink: './send-report',
      detailsSectionStatus: wasteQdrResolveSectionStatus(payload, 'qdr'),
      sendReportSectionStatus: wasteQdrResolveSectionStatus(payload, 'sendReport'),
      changesRequestedSectionStatus: wasteQdrResolveSectionStatus(payload, 'changesRequested'),
      isWaitTask: wasteQdrWaitTasks.includes(requestTaskType),
      warningText: wasteQdrWarningText[requestTaskType],
      sectionsCompleted: wasteQdrSubmitWizardComplete(payload),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
    };
  });

  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
  ) {}
}
