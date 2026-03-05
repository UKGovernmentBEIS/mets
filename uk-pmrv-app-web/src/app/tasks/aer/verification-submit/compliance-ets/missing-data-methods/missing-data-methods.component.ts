import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { missingDataMethodsFormProvider } from '@tasks/aer/verification-submit/compliance-ets/missing-data-methods/missing-data-methods.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-missing-data-methods',
  templateUrl: './missing-data-methods.component.html',
  providers: [missingDataMethodsFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MissingDataMethodsComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../uncertainty-assessment', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      methodsApplyingMissingDataNotUsedReason: this.form.get('methodsApplyingMissingDataUsed').value
        ? null
        : this.form.get('methodsApplyingMissingDataNotUsedReason').value,
    };
  }
}
