import { UntypedFormBuilder } from '@angular/forms';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  CalculationOfCO2ReportingEmissions,
  DreApplicationSubmitRequestTaskPayload,
  MeasurementOfCO2ReportingEmissions,
  MeasurementOfN2OReportingEmissions,
} from 'pmrv-api';

export const monitoringApproachesFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const monitoringApproachTypes: string[] = [];
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: DreApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    if (payload?.dre?.monitoringApproachReportingEmissions) {
      Object.keys(payload?.dre?.monitoringApproachReportingEmissions).forEach((key) => {
        monitoringApproachTypes.push(key);
      });
    }

    return fb.group({
      monitoringApproaches: [
        { value: monitoringApproachTypes, disabled },
        GovukValidators.required('Select at least one monitoring approach'),
      ],
      hasTransferCalculationCO2: [
        {
          value: (
            payload?.dre?.monitoringApproachReportingEmissions?.CALCULATION_CO2 as CalculationOfCO2ReportingEmissions
          )?.calculateTransferredCO2,
          disabled,
        },
        GovukValidators.required('Select an option'),
      ],
      hasTransferMeasurementCO2: [
        {
          value: (
            payload?.dre?.monitoringApproachReportingEmissions?.MEASUREMENT_CO2 as MeasurementOfCO2ReportingEmissions
          )?.measureTransferredCO2,
          disabled,
        },
        GovukValidators.required('Select an option'),
      ],
      hasTransferMeasurementN2O: [
        {
          value: (
            payload?.dre?.monitoringApproachReportingEmissions?.MEASUREMENT_N2O as MeasurementOfN2OReportingEmissions
          )?.measureTransferredN2O,
          disabled,
        },
        GovukValidators.required('Select an option'),
      ],
    });
  },
};
