import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const freeAllocationFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload;
    const bdr = statePayload?.bdr;

    return fb.group({
      isApplicationForFreeAllocation: [
        { value: bdr?.isApplicationForFreeAllocation ?? null, disabled },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select yes if you are applying for free allocation')],
        },
      ],
      statusApplicationType: [
        {
          value: bdr?.statusApplicationType ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select whether you are applying for HSE or USE status')],
        },
      ],
      infoIsCorrectChecked: [
        {
          value: bdr?.infoIsCorrectChecked ? [bdr?.infoIsCorrectChecked] : null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Confirm that the information provided is correct')],
        },
      ],
    });
  },
};
