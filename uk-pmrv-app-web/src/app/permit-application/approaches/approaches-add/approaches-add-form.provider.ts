import { UntypedFormBuilder } from '@angular/forms';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import {
  CalculationOfCO2MonitoringApproach,
  MeasurementOfCO2MonitoringApproach,
  MeasurementOfN2OMonitoringApproach,
} from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';

export const approachesAddFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = [];

    if (store?.permit?.monitoringApproaches) {
      Object.keys(store?.permit?.monitoringApproaches).forEach((key) => {
        value.push(key);
      });
    }

    return fb.group({
      monitoringApproaches: [value, GovukValidators.required('Select a monitoring approach')],
      hasTransferCalculationCO2: [
        (store.permit?.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)?.hasTransfer,
        GovukValidators.required('Select Yes or No'),
      ],
      hasTransferMeasurementCO2: [
        (store.permit?.monitoringApproaches?.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)?.hasTransfer,
        GovukValidators.required('Select Yes or No'),
      ],
      hasTransferMeasurementN2O: [
        (store.permit?.monitoringApproaches?.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)?.hasTransfer,
        GovukValidators.required('Select Yes or No'),
      ],
    });
  },
};
