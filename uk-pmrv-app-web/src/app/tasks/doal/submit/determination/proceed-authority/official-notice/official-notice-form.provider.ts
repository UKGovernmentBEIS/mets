import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';

export const officialNoticeFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const determination = (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)?.doal
      ?.determination as DoalProceedToAuthorityDetermination;

    return fb.group({
      needsOfficialNotice: [
        { value: determination?.needsOfficialNotice ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],
    });
  },
};
