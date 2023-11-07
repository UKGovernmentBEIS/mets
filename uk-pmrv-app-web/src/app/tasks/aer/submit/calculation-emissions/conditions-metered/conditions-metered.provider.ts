import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
} from 'pmrv-api';

import { AER_CALCULATION_EMISSIONS_FORM } from '../calculation-emissions';

export const conditionsMeteredFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

    return fb.group({
      fuelMeteringConditionType: [
        { value: parameterCalculationMethod?.fuelMeteringConditionType ?? 'CELSIUS_15', disabled },
        {
          validators: GovukValidators.required('Select the condition for this source stream metered'),
        },
      ],
    });
  },
};
