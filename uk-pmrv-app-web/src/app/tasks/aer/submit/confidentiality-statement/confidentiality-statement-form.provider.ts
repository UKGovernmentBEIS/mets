import { UntypedFormBuilder } from '@angular/forms';

import { createAnotherSection } from '@shared/components/confidentiality-statement/confidentiality-statement-add';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const confidentialityStatementFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const value = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      .confidentialityStatement;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      confidentialSections: fb.array(
        value?.confidentialSections?.map(createAnotherSection) ?? [createAnotherSection()],
      ),
    });
  },
};
