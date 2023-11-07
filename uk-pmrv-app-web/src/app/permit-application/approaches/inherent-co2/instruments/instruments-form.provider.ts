import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const instrumentsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const installations = (state.permit.monitoringApproaches?.INHERENT_CO2 as InherentCO2MonitoringApproach)
      ?.inherentReceivingTransferringInstallations;

    const measurementInstrumentOwnerTypes = installations
      ? installations[Number(route.snapshot.paramMap.get('index'))]?.measurementInstrumentOwnerTypes ?? null
      : null;

    return fb.group({
      measurementInstrumentOwnerTypes: [
        {
          value: measurementInstrumentOwnerTypes ?? null,
          disabled,
        },
        { validators: GovukValidators.required('Select an option') },
      ],
    });
  },
};
