import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { dataVerificationFormProvider } from '@tasks/aer/verification-submit/compliance-ets/data-verification/data-verification.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-data-verification',
  templateUrl: './data-verification.component.html',
  providers: [dataVerificationFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataVerificationComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../monitoring-approach', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      dataVerificationNotCompletedReason: this.form.get('dataVerificationCompleted').value
        ? null
        : this.form.get('dataVerificationNotCompletedReason').value,
    };
  }
}
