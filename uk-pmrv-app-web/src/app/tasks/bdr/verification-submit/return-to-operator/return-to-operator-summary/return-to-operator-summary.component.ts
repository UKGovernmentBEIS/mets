import { ChangeDetectionStrategy, Component, computed, Inject, Signal, signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';

import { BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  changesRequired: BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload['changesRequired'];
}

@Component({
  selector: 'app-bdr-return-to-operator-summary',
  standalone: true,
  imports: [BdrTaskSharedModule, SharedModule, RouterLink],
  templateUrl: './return-to-operator-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrReturnToOperatorSummaryComponent {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  isSubmitted = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const changesRequired = (this.form.value as BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .changesRequired;

    return {
      isEditable,
      changesRequired,
    };
  });

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
  ) {}

  onConfirm() {
    const formValues: BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload = this.form.value;

    this.bdrService
      .postSubmit('BDR_VERIFICATION_RETURN_TO_OPERATOR', {
        changesRequired: formValues.changesRequired,
      } as BDRApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
