import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { HseTiService } from '@tasks/hseti/core/hseti.service';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { resolveSectionStatus } from '@tasks/hseti/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { RequestTaskDTO } from 'pmrv-api';

import { hsetiExpectedTaskTypes, submitTitle, waitTasks } from './submit';

interface ViewModel {
  title: string;
  subTitle: string;
  notification: boolean;
  expectedTaskType: RequestTaskDTO['type'];
  detailsSectionStatus: TaskItemStatus;
  sendReportSectionStatus: TaskItemStatus;
  redirectDetailsLink: string;
  redirectSendReportLink: string;
  isWaitTask: boolean;
  changedRequestedSectionStatus: TaskItemStatus;
}

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  requestTaskType = this.hseTiService.requestTaskType;
  requestMetadata = this.hseTiService.requestMetadata;
  payload = this.hseTiService.payload;
  allocationPeriod = this.hseTiService.allocationPeriod;
  itemNamePipe = new ItemNamePipe();

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const requestTaskType = this.requestTaskType();
    const subTitle = `${this.allocationPeriod()} HSE target increase details`;

    return {
      title: submitTitle(requestTaskType, this.allocationPeriod()),
      subTitle,
      expectedTaskType: hsetiExpectedTaskTypes.find((type) => type === requestTaskType),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
      redirectDetailsLink: './details/summary',
      redirectSendReportLink:
        resolveSectionStatus(payload, 'sendReport') !== 'cannot start yet' ? './send-report' : null,
      detailsSectionStatus: resolveSectionStatus(payload, 'details'),
      sendReportSectionStatus: resolveSectionStatus(payload, 'sendReport'),
      changedRequestedSectionStatus: resolveSectionStatus(payload, 'changesRequested'),
      isWaitTask: waitTasks.includes(requestTaskType),
    };
  });

  constructor(
    private readonly hseTiService: HseTiService,
    private readonly router: Router,
  ) {}
}
