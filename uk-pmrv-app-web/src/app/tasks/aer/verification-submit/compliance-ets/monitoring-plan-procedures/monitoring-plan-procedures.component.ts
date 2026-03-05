import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { monitoringPlanProceduresFormProvider } from '@tasks/aer/verification-submit/compliance-ets/monitoring-plan-procedures/monitoring-plan-procedures.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-monitoring-plan-procedures',
  templateUrl: './monitoring-plan-procedures.component.html',
  providers: [monitoringPlanProceduresFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanProceduresComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../data-verification', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      proceduresMonitoringPlanNotDocumentedReason: this.form.get('proceduresMonitoringPlanDocumented').value
        ? null
        : this.form.get('proceduresMonitoringPlanNotDocumentedReason').value,
    };
  }
}
