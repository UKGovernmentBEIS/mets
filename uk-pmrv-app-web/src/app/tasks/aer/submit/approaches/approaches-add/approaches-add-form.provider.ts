import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  MeasurementOfCO2Emissions,
  MeasurementOfN2OEmissions,
} from 'pmrv-api';

export const approachesAddFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const value = [];
    const state = store.getValue();

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    if (payload?.aer?.monitoringApproachEmissions) {
      Object.keys(payload?.aer?.monitoringApproachEmissions).forEach((key) => {
        value.push(key);
      });
    }

    return fb.group({
      monitoringApproaches: [
        { value: value, disabled: !state.isEditable },
        GovukValidators.required('Select a monitoring approach'),
      ],
      hasTransferCalculationCO2: [
        {
          value: (payload?.aer?.monitoringApproachEmissions?.CALCULATION_CO2 as CalculationOfCO2Emissions)?.hasTransfer,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
      hasTransferMeasurementCO2: [
        {
          value: (payload?.aer?.monitoringApproachEmissions?.MEASUREMENT_CO2 as MeasurementOfCO2Emissions)?.hasTransfer,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
      hasTransferMeasurementN2O: [
        {
          value: (payload?.aer?.monitoringApproachEmissions?.MEASUREMENT_N2O as MeasurementOfN2OEmissions)?.hasTransfer,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select Yes or No'),
      ],
    });
  },
};
