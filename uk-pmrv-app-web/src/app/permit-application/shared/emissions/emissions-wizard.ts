import { MeasurementOfCO2MeasuredEmissions, MeasurementOfN2OMeasuredEmissions } from 'pmrv-api';

import { SubTaskKey } from '../types/permit-task.type';

export function isWizardComplete(
  taskKey: SubTaskKey,
  measuredEmissions: MeasurementOfN2OMeasuredEmissions | MeasurementOfCO2MeasuredEmissions,
): boolean {
  if (taskKey === 'MEASUREMENT_CO2') {
    return (
      !!measuredEmissions &&
      (measuredEmissions.isHighestRequiredTier ||
        measuredEmissions.tier === 'TIER_4' ||
        measuredEmissions?.noHighestRequiredTierJustification?.isCostUnreasonable ||
        measuredEmissions?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
    );
  } else {
    return (
      !!measuredEmissions &&
      (measuredEmissions.isHighestRequiredTier ||
        measuredEmissions.tier === 'TIER_3' ||
        measuredEmissions?.noHighestRequiredTierJustification?.isCostUnreasonable ||
        measuredEmissions?.noHighestRequiredTierJustification?.isTechnicallyInfeasible)
    );
  }
}
