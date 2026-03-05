import { ChangeDetectionStrategy, Component, Inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { returnToOperatorForChangesFormProvider } from './return-to-operator-for-changes-form.provider';

@Component({
  selector: 'app-aer-verify-return-to-operator-for-changes',
  standalone: true,
  imports: [SharedModule],
  providers: [returnToOperatorForChangesFormProvider],
  templateUrl: './return-to-operator-for-changes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerVerifyReturnToOperatorForChangesComponent {
  isEditable = toSignal(this.aerService.isEditable$);
  isSubmitted = signal(false);

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.aerService
      .postSubmit('AER_VERIFICATION_RETURN_TO_OPERATOR', this.form.value)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.isSubmitted.set(true);
      });
  }
}
