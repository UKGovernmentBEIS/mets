import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const transportApproachFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach)
      .monitoringTransportNetworkApproach;

    return fb.group({
      monitoringTransportNetworkApproach: [
        { value: value ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Enter an approach') },
      ],
    });
  },
};
