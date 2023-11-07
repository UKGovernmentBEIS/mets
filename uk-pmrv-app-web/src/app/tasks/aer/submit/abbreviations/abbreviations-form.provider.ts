import { UntypedFormBuilder } from '@angular/forms';

import { createAbbreviationDefinition } from '@shared/components/abbreviations/abbreviation-definition-form';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const abbreviationsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const item = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.abbreviations;

    return fb.group({
      exist: [
        { value: item?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      abbreviationDefinitions: fb.array(item?.abbreviationDefinitions?.map(createAbbreviationDefinition) ?? []),
    });
  },
};
