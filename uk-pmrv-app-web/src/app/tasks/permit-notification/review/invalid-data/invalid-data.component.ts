import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { decisionFormProvider, REVIEW_FORM } from '../decision/decision-form.provider';

@Component({
  selector: 'app-permit-notification-review-invalid-data',
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>
    <a govukLink routerLink="..">Return to permit notification</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [decisionFormProvider],
})
export class InvalidDataComponent {
  constructor(@Inject(REVIEW_FORM) readonly form: UntypedFormGroup) {}
}
