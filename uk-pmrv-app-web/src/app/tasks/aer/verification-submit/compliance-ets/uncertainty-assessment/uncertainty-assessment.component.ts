import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';
import { uncertaintyAssessmentFormProvider } from '@tasks/aer/verification-submit/compliance-ets/uncertainty-assessment/uncertainty-assessment.form.provider';

@Component({
  selector: 'app-uncertainty-assessment',
  templateUrl: './uncertainty-assessment.component.html',
  providers: [uncertaintyAssessmentFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncertaintyAssessmentComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../competent-authority-guidance', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      uncertaintyAssessmentNotUsedReason: this.form.get('uncertaintyAssessment').value
        ? null
        : this.form.get('uncertaintyAssessmentNotUsedReason').value,
    };
  }
}
