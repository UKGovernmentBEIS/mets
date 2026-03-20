import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { DRE_TASK_FORM } from '../core/dre-task-form.token';
import { feeFormProvider } from '../submit/fee/fee-form.provider';

@Component({
  selector: 'app-invalid-data',
  standalone: false,
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>
    <a govukLink routerLink="..">Return to Reportable emissions</a>
  `,
  providers: [feeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidDataComponent {
  constructor(@Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup) {}
}
