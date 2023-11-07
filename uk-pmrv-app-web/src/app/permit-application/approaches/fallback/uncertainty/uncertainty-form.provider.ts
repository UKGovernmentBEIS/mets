import { UntypedFormBuilder } from '@angular/forms';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const uncertaintyFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();

    return fb.group(
      ProcedureFormComponent.controlsFactory(
        (state.permit.monitoringApproaches?.FALLBACK as FallbackMonitoringApproach).annualUncertaintyAnalysis,
        !state.isEditable,
      ),
    );
  },
};
