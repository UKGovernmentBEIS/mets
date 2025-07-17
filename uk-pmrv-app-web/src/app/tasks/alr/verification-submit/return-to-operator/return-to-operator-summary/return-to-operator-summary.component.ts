import { ChangeDetectionStrategy, Component, computed, Inject, Signal, signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  changesRequired: ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload['changesRequired'];
}

@Component({
  selector: 'app-alr-return-to-operator-summary',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule, RouterLink],
  templateUrl: './return-to-operator-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReturnToOperatorSummaryComponent {
  isEditable: Signal<boolean> = this.alrService.isEditable;
  isSubmitted = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const changesRequired = (this.form.value as ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .changesRequired;

    return {
      isEditable,
      changesRequired,
    };
  });

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
  ) {}

  onConfirm() {
    const formValues: ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload = this.form.value;

    this.alrService
      .postAlrSubmit('ALR_VERIFICATION_RETURN_TO_OPERATOR', {
        changesRequired: formValues.changesRequired,
      } as ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
