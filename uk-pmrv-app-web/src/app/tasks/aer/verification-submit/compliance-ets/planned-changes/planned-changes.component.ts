import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { plannedChangesFormProvider } from '@tasks/aer/verification-submit/compliance-ets/planned-changes/planned-changes.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-planned-changes',
  templateUrl: './planned-changes.component.html',
  providers: [plannedChangesFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlannedChangesComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../missing-data-methods', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      plannedActualChangesNotReportedReason: this.form.get('plannedActualChangesReported').value
        ? null
        : this.form.get('plannedActualChangesNotReportedReason').value,
    };
  }
}
