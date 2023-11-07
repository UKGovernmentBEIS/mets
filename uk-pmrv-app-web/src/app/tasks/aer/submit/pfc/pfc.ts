import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

export const AER_PFC_FORM = new InjectionToken<UntypedFormGroup>('Aer calculation emissions form');

export function buildTaskData(payload: AerApplicationSubmitRequestTaskPayload, sourceStreamEmissions) {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const calculation = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

  const data = {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      CALCULATION_PFC: {
        ...calculation,
        type: 'CALCULATION_PFC',
        sourceStreamEmissions: sourceStreamEmissions,
      },
    },
  };

  return data;
}
