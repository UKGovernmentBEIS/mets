import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export const REVIEW_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Permit surrender decision form');

export const reviewDecisionFormProvider = {
  provide: REVIEW_DECISION_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const reviewDecision = store.getState().reviewDecision;

    return fb.group({
      type: [reviewDecision?.type ?? null, GovukValidators.required('Select a decision for this review group')],
      notes: [
        reviewDecision?.details?.notes ?? null,
        [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    });
  },
};
