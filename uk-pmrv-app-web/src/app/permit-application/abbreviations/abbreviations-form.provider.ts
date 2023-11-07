import { UntypedFormBuilder } from '@angular/forms';

import { createAbbreviationDefinition } from '@shared/components/abbreviations/abbreviation-definition-form';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const abbreviationsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = store.permit.abbreviations;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      abbreviationDefinitions: fb.array(value?.abbreviationDefinitions?.map(createAbbreviationDefinition) ?? []),
    });
  },
};
