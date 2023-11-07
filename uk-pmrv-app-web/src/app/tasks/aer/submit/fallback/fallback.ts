import BigNumber from 'bignumber.js';

import { AerApplicationSubmitRequestTaskPayload, AerMonitoringApproachEmissions, FallbackEmissions } from 'pmrv-api';

export function buildTaskData(
  payload: AerApplicationSubmitRequestTaskPayload,
  newFallbackEmissions: Partial<FallbackEmissions>,
): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const existingFallbackEmissions = monitoringApproachEmissions?.['FALLBACK'] as FallbackEmissions;

  return {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      FALLBACK: {
        type: 'FALLBACK',
        ...existingFallbackEmissions,
        ...newFallbackEmissions,
      } as FallbackEmissions,
    },
  };
}

export function calculateFallbackReportableEmissions(
  contains: boolean,
  totalFossilEmissions: string | null,
  totalNonSustainableBiomassEmissions: string | null,
): string {
  const fossilEmissions = new BigNumber(totalFossilEmissions ?? 0);
  return contains
    ? fossilEmissions.plus(new BigNumber(totalNonSustainableBiomassEmissions ?? 0)).toString()
    : fossilEmissions.toString();
}
