import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ConfidentialSection } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const confidentialityStatementFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = store.permit.confidentialityStatement;

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

export function createAnotherSection(value?: ConfidentialSection): UntypedFormGroup {
  return new UntypedFormGroup({
    section: new UntypedFormControl(value?.section ?? null, [
      GovukValidators.required('Enter a section'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    explanation: new UntypedFormControl(value?.explanation ?? null, [
      GovukValidators.required('Enter an explanation'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
