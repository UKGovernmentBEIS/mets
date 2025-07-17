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

export const deliveryZoneFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

    return fb.group({
      localZoneCode: [
        { value: parameterCalculationMethod?.localZoneCode ?? null, disabled },
        {
          validators: [GovukValidators.required('Select your local delivery zone')],
        },
      ],
    });
  },
};
