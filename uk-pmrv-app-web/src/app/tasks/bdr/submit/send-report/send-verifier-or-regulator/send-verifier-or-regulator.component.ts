import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { submitWizardComplete } from '../../submit.wizard';
import { sendVerifierOrRegulatorFormProvider } from './send-verifier-or-regulator.provider';

@Component({
  selector: 'app-send-verifier-or-regulator',
  templateUrl: './send-verifier-or-regulator.component.html',
  providers: [sendVerifierOrRegulatorFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendVerifierOrRegulatorComponent implements PendingRequest {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  bdrPayload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;

  isSendReportAvailable: Signal<boolean> = computed(() => {
    const payload = this.bdrPayload();
    return submitWizardComplete(payload);
  });

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly bdrService: BdrService,
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
