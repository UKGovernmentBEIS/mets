/**
 * METS API Documentation
 * METS API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ReportableAndBiomassEmission } from './reportableAndBiomassEmission';
import { ReportableEmission } from './reportableEmission';

export interface MonitoringApproachTypeEmissions {
  calculationCombustionEmissions: ReportableAndBiomassEmission;
  calculationProcessEmissions: ReportableAndBiomassEmission;
  calculationMassBalanceEmissions: ReportableAndBiomassEmission;
  calculationTransferredCO2Emissions: ReportableAndBiomassEmission;
  measurementCO2Emissions: ReportableAndBiomassEmission;
  measurementTransferredCO2Emissions: ReportableAndBiomassEmission;
  measurementN2OEmissions: ReportableAndBiomassEmission;
  measurementTransferredN2OEmissions: ReportableAndBiomassEmission;
  fallbackEmissions: ReportableAndBiomassEmission;
  calculationPFCEmissions: ReportableEmission;
  inherentCO2Emissions: ReportableEmission;
}
