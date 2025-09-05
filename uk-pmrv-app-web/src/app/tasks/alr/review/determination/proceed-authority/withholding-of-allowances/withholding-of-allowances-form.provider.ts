import { AbstractControl, UntypedFormBuilder, ValidatorFn } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

export const alrWithholdingOfAllowancesFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const determination = (
      state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewOutcome?.determination as DoalProceedToAuthorityDetermination;

    return fb.group(
      {
        hasWithholdingOfAllowances: [
          {
            value: determination?.hasWithholdingOfAllowances ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select yes or no')],
          },
        ],
        noticeIssuedDate: [
          {
            value: determination?.withholdingAllowancesNotice?.noticeIssuedDate
              ? new Date(determination?.withholdingAllowancesNotice?.noticeIssuedDate)
              : null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Enter a date'), todayOrPastDateValidator()],
          },
        ],
        withholdingOfAllowancesComment: [
          {
            value: determination?.withholdingAllowancesNotice?.withholdingOfAllowancesComment ?? null,
            disabled,
          },
          {
            validators: [
              GovukValidators.required('Enter a comment'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ],
          },
        ],
      },
      {
        updateOn: 'change',
      },
    );
  },
};

const todayOrPastDateValidator = (): ValidatorFn => {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value >= new Date()
      ? { invalidDate: 'The date must be today or in the past' }
      : null;
  };
};
