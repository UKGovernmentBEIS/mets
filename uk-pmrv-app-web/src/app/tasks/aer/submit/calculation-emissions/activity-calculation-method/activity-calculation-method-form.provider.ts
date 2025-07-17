import { Injectable, InjectionToken } from '@angular/core';
import { Router } from '@angular/router';

import { getActiveRoute } from '@core/navigation/navigation.util';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
} from 'pmrv-api';

export const ACTIVITY_CALCULATION_METHOD_FORM_PROVIDER = new InjectionToken<any>(
  'ACTIVITY_CALCULATION_METHOD_FORM_PROVIDER',
);

@Injectable()
export class ActivityCalculationMethodFormProvider {
  constructor(
    private store: CommonTasksStore,
    private router: Router,
  ) {}

  getActivityCalculationMethodType() {
    const state = this.store.getValue();
    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    const activeRoute = getActiveRoute(this.router, true);
    const sourceStreamEmission = activeRoute.params.index
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(activeRoute.params.index)
        ]
      : null;

    const parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

    return parameterCalculationMethod?.calculationActivityDataCalculationMethod?.type;
  }
}
