/**
 * PMRV API Documentation
 * PMRV API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { BiomassPercentages } from './biomassPercentages';
import { CalculationParameterCalculationMethod } from './calculationParameterCalculationMethod';
import { CalculationParameterMonitoringTier } from './calculationParameterMonitoringTier';
import { DurationRange } from './durationRange';
import { ParameterMonitoringTierDiffReason } from './parameterMonitoringTierDiffReason';
import { TransferCO2 } from './transferCO2';

export interface CalculationSourceStreamEmission {
  id?: string;
  sourceStream: string;
  emissionSources: Array<string>;
  parameterMonitoringTiers: Array<CalculationParameterMonitoringTier>;
  biomassPercentages: BiomassPercentages;
  durationRange: DurationRange;
  parameterMonitoringTierDiffReason?: ParameterMonitoringTierDiffReason;
  parameterCalculationMethod: CalculationParameterCalculationMethod;
  transfer?: TransferCO2;
}
