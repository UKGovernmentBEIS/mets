import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const allowancesFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;
    const disabled = !state.isEditable;

    return fb.group({
      allowancesSurrenderRequired: [
        { value: reviewDetermination?.allowancesSurrenderRequired ?? null, disabled },
        GovukValidators.required('Select yes or no'),
      ],
      allowancesSurrenderDate: [
        {
          value: reviewDetermination?.allowancesSurrenderDate
            ? new Date(reviewDetermination.allowancesSurrenderDate)
            : null,
          disabled,
        },
      ],
    });
  },
};
