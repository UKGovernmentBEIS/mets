import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { GovukValidators } from 'govuk-components';

export const naceCodeSubCategoryFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) =>
    fb.group({
      subCategory: [null, GovukValidators.required('Enter the sub-category')],
    }),
};
