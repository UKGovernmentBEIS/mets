import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { ProcedureOptionalForm, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const deductionsToAmountFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const state = store.getValue();
    const keys = route.snapshot.data.taskKey.split('.');
    const taskKey = keys[keys.length - 1];
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach)[
      taskKey
    ] as ProcedureOptionalForm;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      procedureForm: fb.group(ProcedureFormComponent.controlsFactory(value?.procedureForm)),
    });
  },
};
