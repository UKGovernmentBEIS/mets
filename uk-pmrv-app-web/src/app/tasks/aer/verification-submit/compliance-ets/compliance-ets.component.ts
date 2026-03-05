import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { complianceEtsFormProvider } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-compliance-ets',
  templateUrl: './compliance-ets.component.html',
  providers: [complianceEtsFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceEtsComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('regulation-monitoring-reporting', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      monitoringPlanRequirementsNotMetReason: this.form.get('monitoringPlanRequirementsMet').value
        ? null
        : this.form.get('monitoringPlanRequirementsNotMetReason').value,
    };
  }
}
