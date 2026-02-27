import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';

@Component({
  selector: 'app-invalid-data',
  standalone: false,
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>
    <a govukLink routerLink="..">Return to permit revocation</a>
  `,
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidDataComponent {
  constructor(@Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup) {}
}
