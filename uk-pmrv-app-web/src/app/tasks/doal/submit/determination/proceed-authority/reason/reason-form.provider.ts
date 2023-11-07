import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';

export const reasonFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const determination = (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)?.doal
      ?.determination as DoalProceedToAuthorityDetermination;

    return fb.group(
      {
        articleReasonGroupType: [
          {
            value: determination?.articleReasonGroupType ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        article6aReasons: [
          {
            value: determination?.articleReasonItems ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        article34HReasonItems: [
          {
            value: determination?.articleReasonItems ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        reason: [
          { value: determination?.reason ?? null, disabled },
          {
            validators: [
              GovukValidators.required('Enter a comment'),
              GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
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
