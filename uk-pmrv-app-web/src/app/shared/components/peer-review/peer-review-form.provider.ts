import { FactoryProvider, InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const PEER_REVIEW_FORM = new InjectionToken<UntypedFormGroup>('Notify operator form');

export const peerReviewFormFactory: FactoryProvider = {
  provide: PEER_REVIEW_FORM,
  useFactory: (fb: UntypedFormBuilder) =>
    fb.group({
      assignees: [null, GovukValidators.required('Select a peer reviewer')],
    }),
  deps: [UntypedFormBuilder],
};
