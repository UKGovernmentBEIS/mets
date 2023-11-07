import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { FOLLOW_UP_REVIEW_DECISION_FORM, followUpReviewDecisionFormProvider } from '../decision/decision-form.provider';

@Component({
  selector: 'app-permit-notification-follow-up-review-invalid-data',
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>
    <a govukLink routerLink=".."> Return to permit notification follow up</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [followUpReviewDecisionFormProvider],
})
export class InvalidDataComponent {
  constructor(@Inject(FOLLOW_UP_REVIEW_DECISION_FORM) readonly form: UntypedFormGroup) {}
}
