import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationSubmitRequestTaskPayload, BDRS2RequestMetadata, RequestMetadata } from 'pmrv-api';

import { BdrS2Service } from '../core';
import { BdrS2TaskSharedModule } from '../shared';
import { submitVerificationWizardComplete } from './verification.wizard';

@Component({
  selector: 'app-verification-submit',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  templateUrl: './verification-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationSubmitComponent {
  requestTaskType = toSignal(this.store.requestTaskType$);
  requestMetadata: Signal<RequestMetadata> = this.bdrs2Service.requestMetadata;
  title: Signal<string> = computed(() => {
    const requestMetadata = this.requestMetadata();
    return 'Verify ' + (requestMetadata as BDRS2RequestMetadata)?.year + ' stage 2 baseline data report';
  });
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  daysRemaining: Signal<number> = this.bdrs2Service.daysRemaining;

  sectionsCompleted: Signal<boolean> = computed(() => {
    const payload = this.bdrs2Payload();
    return submitVerificationWizardComplete(payload);
  });

  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
  ) {}
}
