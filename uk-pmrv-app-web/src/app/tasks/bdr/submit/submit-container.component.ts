import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRRequestMetadata, RequestTaskDTO } from 'pmrv-api';

import { BdrTaskSharedModule } from '../shared/bdr-task-shared.module';
import { BdrService } from '../shared/services/bdr.service';
import { bdrExpectedTaskTypes, submitTitle, waitTasks, warningText } from './submit';
import { submitWizardComplete } from './submit.wizard';

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
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  requestTaskType = this.bdrService.requestTaskType;
  requestMetadata = this.bdrService.requestMetadata;
  payload = this.bdrService.payload;
  daysRemaining = this.bdrService.daysRemaining;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskType = this.requestTaskType();
    const requestMetadata = this.requestMetadata();
    const payload = this.payload();

    return {
      expectedTaskType: bdrExpectedTaskTypes.find((type) => type === requestTaskType),
      title: submitTitle(requestTaskType, (requestMetadata as BDRRequestMetadata)?.year),
      notification: this.router.getCurrentNavigation()?.extras.state?.notification,
      daysRemaining: this.daysRemaining(),
      sectionsCompleted: submitWizardComplete(payload),
      isWaitTask: waitTasks.includes(requestTaskType),
      warningText: warningText[requestTaskType],
    };
  });

  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
  ) {}
}
