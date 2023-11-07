import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { regulationMonitoringReportingFormProvider } from '@tasks/aer/verification-submit/compliance-ets/regulation-monitoring-reporting/regulation-monitoring-reporting.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-regulation-monitoring-reporting',
  templateUrl: './regulation-monitoring-reporting.component.html',
  providers: [regulationMonitoringReportingFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulationMonitoringReportingComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../detail-source-data', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      euRegulationMonitoringReportingNotMetReason: this.form.get('euRegulationMonitoringReportingMet').value
        ? null
        : this.form.get('euRegulationMonitoringReportingNotMetReason').value,
    };
  }
}
