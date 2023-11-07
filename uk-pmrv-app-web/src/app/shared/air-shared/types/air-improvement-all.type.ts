import {
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementFallback,
  AirImprovementMeasurement,
} from 'pmrv-api';

export type AirImprovementAll = AirImprovementCalculationCO2 &
  AirImprovementCalculationPFC &
  AirImprovementMeasurement &
  AirImprovementFallback;
