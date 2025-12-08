import { UntypedFormBuilder } from '@angular/forms';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { WASTE_QDR_TASK_FORM } from '@tasks/waste-qdr/core';

import { GovukValidators } from 'govuk-components';

import { WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const provideQdrFormProvider = {
  provide: WASTE_QDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const qdr = (state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)?.qdr;

    return fb.group({
      reportProvided: [
        { value: qdr?.reportProvided ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],
      reasonForUnprovided: [
        { value: qdr?.reasonForUnprovided ?? null, disabled },
        { validators: [GovukValidators.required('Enter a reason')] },
      ],
    });
  },
};
