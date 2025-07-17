import { UntypedFormBuilder } from '@angular/forms';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { Procedure } from 'pmrv-api';

import { MmpProcedureFormComponent } from '../mmp-procedure-form/mmp-procedure-form.component';

export const DataFlowActivitiesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = (state.permit.monitoringMethodologyPlans?.digitizedPlan?.procedures?.['DATA_FLOW_ACTIVITIES'] ||
      {}) as Procedure;

    return fb.group(MmpProcedureFormComponent.controlsFactory(value, !state.isEditable));
  },
};
