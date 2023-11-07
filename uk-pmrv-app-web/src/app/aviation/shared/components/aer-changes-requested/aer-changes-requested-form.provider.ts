import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

export const AER_CHANGES_REQUESTED_FORM = new InjectionToken<UntypedFormGroup>('Aer changes requested form');

export const AerChangesRequestedFormProvider = {
  provide: AER_CHANGES_REQUESTED_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore) => {
    const state = store.getValue();

    const changesRequested = (state.requestTaskItem.requestTask.payload as AerRequestTaskPayload)?.aerSectionsCompleted
      ?.changesRequested;

    return fb.group(
      {
        changes: [
          changesRequested,
          GovukValidators.required('You must confirm that you have made the changes the regulator has requested'),
        ],
      },
      { updateOn: 'change' },
    );
  },
};
