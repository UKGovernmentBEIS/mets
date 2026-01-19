import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const detailsFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload;
    const bdrs2 = statePayload?.bdrs2;

    return fb.group({
      covidAdjustments: [
        { value: bdrs2?.bdrs2guardQuestions?.covidAdjustments ?? null, disabled },
        {
          updateOn: 'change',
          validators: [
            GovukValidators.required(
              'Select yes if you made COVID adjustments that you want to be excluded from your HAL calculation',
            ),
          ],
        },
      ],
      inEiteSector: [
        { value: bdrs2?.bdrs2guardQuestions?.inEiteSector ?? null, disabled },
        {
          updateOn: 'change',
          validators: [
            GovukValidators.required(
              'Select yes if your installation is in the aluminium, cement, fertiliser, hydrogen, iron or steel sector',
            ),
          ],
        },
      ],
    });
  },
};
