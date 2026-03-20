import { ChangeDetectionStrategy, Component, computed, Inject, Signal, signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';

import { BDRS2ApplicationVerificationReturnToOperatorRequestTaskActionPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  changesRequired: BDRS2ApplicationVerificationReturnToOperatorRequestTaskActionPayload['changesRequired'];
}

@Component({
  selector: 'app-bdrs2-return-to-operator-summary',
  imports: [BdrS2TaskSharedModule, SharedModule, RouterLink],
  standalone: true,
  templateUrl: './return-to-operator-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2ReturnToOperatorSummaryComponent {
  isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  isSubmitted = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const changesRequired = (this.form.value as BDRS2ApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .changesRequired;

    return {
      isEditable,
      changesRequired,
    };
  });

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
  ) {}

  onConfirm() {
    const formValues: BDRS2ApplicationVerificationReturnToOperatorRequestTaskActionPayload = this.form.value;
    this.bdrs2Service
      .postSubmit('BDRS2_VERIFICATION_RETURN_TO_OPERATOR', {
        changesRequired: formValues.changesRequired,
      } as BDRS2ApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
