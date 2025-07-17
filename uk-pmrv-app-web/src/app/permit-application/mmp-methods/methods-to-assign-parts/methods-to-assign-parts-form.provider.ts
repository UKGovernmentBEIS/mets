import { UntypedFormBuilder } from '@angular/forms';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

export const MethodsToAssignPartsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = state.permit.monitoringMethodologyPlans.digitizedPlan;

    return fb.group({
      assignParts: [
        {
          value: value?.methodTask?.assignParts ?? null,
          disabled: !state.isEditable,
        },
        {
          validators: GovukValidators.required(
            'Enter a description of the methods used to assign parts of installations and their emissions to sub-installations',
          ),
        },
      ],
    });
  },
};
