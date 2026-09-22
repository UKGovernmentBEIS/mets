import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { Router } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { FeeUpdateDraft } from '../fees.model';
import { CHANGE_PAYMENT_AMOUNT_FORM } from './change-payment-amount-form.token';

export const FUTURE_DATE_MESSAGE = 'The date that the payment amount should change must be in the future';

export const changePaymentAmountFormProvider = {
  provide: CHANGE_PAYMENT_AMOUNT_FORM,
  deps: [UntypedFormBuilder, Router],
  useFactory: (formBuilder: UntypedFormBuilder, router: Router): UntypedFormGroup => {
    const draft = router.currentNavigation()?.extras.state as FeeUpdateDraft | undefined;

    return formBuilder.group(
      {
        amount: [
          draft?.amount ?? null,
          [
            GovukValidators.required('Enter a new payment amount in pounds, like 1500 or 1500.50'),
            GovukValidators.notNaN('New payment amount must be a number, like 1500 or 1500.50'),
            GovukValidators.positiveNumber('New payment amount must be more than 0'),
          ],
        ],
        changeWhen: [
          draft?.changeWhen ?? null,
          [GovukValidators.required('Select when the payment amount should change')],
        ],
        // Only restore a previously-entered future date. When the draft's changeWhen was
        // 'immediately', its effectiveDate is the computed current date (not a user entry) and
        // must not be used to prefill this field if the user switches back to 'future'.
        effectiveDate: [
          draft?.changeWhen === 'future' ? (draft?.effectiveDate ?? null) : null,
          [futureDateValidator()],
        ],
      },
      { updateOn: 'change' },
    );
  },
};

function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl) => {
    const now = new Date();
    const today = new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate()));
    return control.value && control.value <= today ? { futureDate: FUTURE_DATE_MESSAGE } : null;
  };
}
