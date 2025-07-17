import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { GovukValidators } from 'govuk-components';

export const returnToOperatorForChangesFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) => {
    return fb.group({
      changesRequired: [null, GovukValidators.required('Enter the changes required')],
    });
  },
};
