import { UntypedFormBuilder } from '@angular/forms';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const stopDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

    return fb.group({
      stopDate: [
        {
          value: reviewDetermination?.stopDate ? new Date(reviewDetermination.stopDate) : null,
          disabled: !state.isEditable,
        },
      ],
    });
  },
};
