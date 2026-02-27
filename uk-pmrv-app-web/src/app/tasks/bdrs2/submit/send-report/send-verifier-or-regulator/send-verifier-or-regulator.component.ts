import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { submitWizardComplete } from '@tasks/bdrs2/utils';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { bdrs2SendVerifierOrRegulatorFormProvider } from './send-verifier-or-regulator-form.provider';

@Component({
  selector: 'app-bdrs2-send-verifier-or-regulator',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './send-verifier-or-regulator.component.html',
  providers: [bdrs2SendVerifierOrRegulatorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2SendVerifierOrRegulatorComponent implements PendingRequest {
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  returnLinkTitle = this.bdrs2Service.title();

  isSendReportAvailable: Signal<boolean> = computed(() => {
    const payload = this.bdrs2Payload();
    return submitWizardComplete(payload);
  });

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (this.form.get('needsVerification').value) {
      this.router.navigate(['verifier'], { relativeTo: this.route, queryParams: { sendTo: 'verifier' } });
    } else {
      this.router.navigate(['regulator'], { relativeTo: this.route, queryParams: { sendTo: 'regulator' } });
    }
  }
}
