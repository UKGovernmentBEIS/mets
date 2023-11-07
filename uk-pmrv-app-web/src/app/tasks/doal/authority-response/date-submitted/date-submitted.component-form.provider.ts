import { UntypedFormBuilder } from '@angular/forms';

import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalAuthorityResponseRequestTaskPayload } from 'pmrv-api';

export const dateSubmittedComponentFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const dateSubmittedToAuthority = (
      state.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload
    )?.doalAuthority?.dateSubmittedToAuthority;

    return fb.group({
      date: [
        {
          value: dateSubmittedToAuthority?.date ? new Date(dateSubmittedToAuthority.date) : null,
          disabled: !state.isEditable,
        },
      ],
    });
  },
};
