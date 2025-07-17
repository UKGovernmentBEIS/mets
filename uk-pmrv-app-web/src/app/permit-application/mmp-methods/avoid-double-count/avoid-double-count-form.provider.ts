import { UntypedFormBuilder } from '@angular/forms';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

export const AvoidDoubleCountFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = state.permit.monitoringMethodologyPlans.digitizedPlan;

    return fb.group({
      avoidDoubleCount: [
        {
          value: value?.methodTask?.avoidDoubleCount ?? '',
          disabled: !state.isEditable,
        },
      ],
    });
  },
};
