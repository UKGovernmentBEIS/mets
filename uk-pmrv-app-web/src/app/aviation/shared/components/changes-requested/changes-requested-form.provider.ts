import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  RequestTaskStore,
} from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

export const CHANGES_REQUESTED_FORM = new InjectionToken<UntypedFormGroup>('Changes requested form');

export const ChangesRequestedFormProvider = {
  provide: CHANGES_REQUESTED_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore) => {
    const state = store.getValue();

    const changesRequested = (
      state.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia | EmpRequestTaskPayloadUkEts
    )?.empSectionsCompleted?.changesRequested;

    return fb.group(
      {
        changes: [
          changesRequested,
          GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
        ],
      },
      { updateOn: 'change' },
    );
  },
};
