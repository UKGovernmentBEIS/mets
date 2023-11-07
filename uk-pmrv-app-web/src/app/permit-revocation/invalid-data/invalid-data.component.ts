import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';

@Component({
  selector: 'app-invalid-data',
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>
    <a govukLink routerLink=".."> Return to permit revocation </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationFormProvider],
})
export class InvalidDataComponent {
  constructor(@Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup) {}
}
