import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { submitWizardComplete } from '@tasks/bdrs2/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRRequestMetadata, RequestTaskDTO } from 'pmrv-api';

import { BdrS2TaskSharedModule } from '../shared';
import { bdrS2ExpectedTaskTypes, submitTitle, waitTasks, warningText } from './submit';

interface ViewModel {
  expectedTaskType: RequestTaskDTO['type'];
  title: string;
  notification: boolean;
  daysRemaining: number;
  sectionsCompleted: boolean;
  isWaitTask: boolean;
  warningText: string;
}

@Component({
  selector: 'app-submit',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitComponent {
  requestTaskType = this.bdrS2Service.requestTaskType;
  requestMetadata = this.bdrS2Service.requestMetadata;
  payload = this.bdrS2Service.payload;
  daysRemaining = this.bdrS2Service.daysRemaining;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();
    const payload = this.payload();

    return {
      expectedTaskType: bdrS2ExpectedTaskTypes.find((type) => type === requestTaskType),
      title: submitTitle(requestTaskType, (requestMetadata as BDRRequestMetadata)?.year),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
      daysRemaining: this.daysRemaining(),
      sectionsCompleted: submitWizardComplete(payload),
      isWaitTask: waitTasks.includes(requestTaskType),
      warningText: warningText[requestTaskType],
    };
  });

  constructor(
    private readonly bdrS2Service: BdrS2Service,
    private readonly router: Router,
  ) {}
}
