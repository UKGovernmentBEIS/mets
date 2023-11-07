import { InjectionToken } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CommonTasksStore } from '../../../store/common-tasks.store';

export const REVIEW_FORM = new InjectionToken<UntypedFormGroup>('Review form');

export const decisionFormProvider = {
  provide: REVIEW_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getState();

    const { reviewDecision } = state.requestTaskItem.requestTask.payload as any;

    return fb.group({
      type: [
        reviewDecision?.type === 'ACCEPTED' ? true : reviewDecision?.type === 'REJECTED' ? false : null,
        { validators: GovukValidators.required('Select a decision'), updateOn: 'change' },
      ],
      officialNotice: [
        reviewDecision?.details?.officialNotice ?? null,
        [GovukValidators.required('Enter a summary'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      followUpResponseRequired: [
        reviewDecision?.details?.followUp?.followUpResponseRequired ?? null,
        { validators: GovukValidators.required('Select yes or no') },
      ],
      followUpRequest: [
        reviewDecision?.details?.followUp?.followUpRequest ?? null,
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      followUpResponseExpirationDate: [
        reviewDecision?.details?.followUp?.followUpResponseExpirationDate
          ? new Date(reviewDecision?.details?.followUp?.followUpResponseExpirationDate)
          : null,
        [futureDateValidator()],
      ],
      notes: [reviewDecision?.details?.notes ?? null, GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
    });
  },
};

function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date() ? { invalidDate: `The date must be in the future` } : null;
  };
}
