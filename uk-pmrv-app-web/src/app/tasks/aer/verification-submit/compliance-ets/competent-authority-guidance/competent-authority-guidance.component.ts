import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { competentAuthorityGuidanceFormProvider } from '@tasks/aer/verification-submit/compliance-ets/competent-authority-guidance/competent-authority-guidance.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-competent-authority-guidance',
  templateUrl: './competent-authority-guidance.component.html',
  providers: [competentAuthorityGuidanceFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompetentAuthorityGuidanceComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../non-conformities', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      competentAuthorityGuidanceNotMetReason: this.form.get('competentAuthorityGuidanceMet').value
        ? null
        : this.form.get('competentAuthorityGuidanceNotMetReason').value,
    };
  }
}
