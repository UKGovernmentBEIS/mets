import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { monitoringApproachFormProvider } from '@tasks/aer/verification-submit/compliance-ets/monitoring-approach/monitoring-approach.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-monitoring-approach',
  templateUrl: './monitoring-approach.component.html',
  providers: [monitoringApproachFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../planned-changes', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      monitoringApproachNotAppliedCorrectlyReason: this.form.get('monitoringApproachAppliedCorrectly').value
        ? null
        : this.form.get('monitoringApproachNotAppliedCorrectlyReason').value,
    };
  }
}
