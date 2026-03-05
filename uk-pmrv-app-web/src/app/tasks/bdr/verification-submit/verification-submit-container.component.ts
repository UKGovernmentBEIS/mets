import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationSubmitRequestTaskPayload, BDRRequestMetadata, RequestMetadata } from 'pmrv-api';

import { BdrTaskSharedModule } from '../shared/bdr-task-shared.module';
import { BdrService } from '../shared/services/bdr.service';
import { submitVerificationWizardComplete } from './verification.wizard';

@Component({
  selector: 'app-verification-submit-container',
  templateUrl: './verification-submit-container.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationSubmitContainerComponent {
  requestTaskType = toSignal(this.store.requestTaskType$);
  requestMetadata: Signal<RequestMetadata> = this.bdrService.requestMetadata;
  title: Signal<string> = computed(() => {
    const requestMetadata = this.requestMetadata();
    return 'Verify ' + (requestMetadata as BDRRequestMetadata)?.year + ' baseline data report';
  });
  bdrPayload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  daysRemaining: Signal<number> = this.bdrService.daysRemaining;

  sectionsCompleted: Signal<boolean> = computed(() => {
    const payload = this.bdrPayload();
    return submitVerificationWizardComplete(payload);
  });

  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
  ) {}
}
