import { AbstractControl, UntypedFormBuilder, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';

export const withholdingFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const determination = (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)?.doal
      ?.determination as DoalProceedToAuthorityDetermination;

    return fb.group({
      hasWithholdingOfAllowances: [
        { value: determination?.hasWithholdingOfAllowances ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],

      withholdingAllowancesNotice: fb.group({
        noticeIssuedDate: [
          {
            value: determination?.withholdingAllowancesNotice?.noticeIssuedDate
              ? new Date(determination?.withholdingAllowancesNotice?.noticeIssuedDate)
              : null,
            disabled,
          },
          { validators: [todayOrPastDateValidator()] },
        ],
        withholdingOfAllowancesComment: [
          { value: determination?.withholdingAllowancesNotice?.withholdingOfAllowancesComment ?? null, disabled },
          {
            validators: [
              GovukValidators.required('Enter a comment'),
              GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
            ],
          },
        ],
      }),
    });
  },
};

function todayOrPastDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value >= new Date()
      ? { invalidDate: 'The date must be today or in the past' }
      : null;
  };
}
