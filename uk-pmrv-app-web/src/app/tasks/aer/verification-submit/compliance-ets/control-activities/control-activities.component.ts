import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { controlActivitiesFormProvider } from '@tasks/aer/verification-submit/compliance-ets/control-activities/control-activities.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-control-activities',
  templateUrl: './control-activities.component.html',
  providers: [controlActivitiesFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ControlActivitiesComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../monitoring-plan-procedures', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      controlActivitiesNotDocumentedReason: this.form.get('controlActivitiesDocumented').value
        ? null
        : this.form.get('controlActivitiesNotDocumentedReason').value,
    };
  }
}
