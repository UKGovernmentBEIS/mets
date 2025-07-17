import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { addDays, format, isAfter, startOfDay } from 'date-fns';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

export const noticeDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const reviewDetermination = state.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

    return fb.group({
      noticeDate: [
        {
          value: reviewDetermination?.noticeDate ? new Date(reviewDetermination.noticeDate) : null,
          disabled: !state.isEditable,
        },
        {
          validators: [minDateValidator()],
        },
      ],
    });
  },
};

function minDateValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors | null => {
    const govukDatePipe = new GovukDatePipe();
    const after28Days = startOfDay(addDays(new Date(), 28));

    return isAfter(group.value, after28Days)
      ? null
      : {
          invalidNoticeDate: `The date must be the same as or after ${govukDatePipe.transform(
            format(addDays(after28Days, 1), 'yyyy-MM-dd'),
          )}`,
        };
  };
}
