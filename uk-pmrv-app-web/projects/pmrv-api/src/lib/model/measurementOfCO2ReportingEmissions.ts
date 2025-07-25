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
import { DreMonitoringApproachReportingEmissions } from './dreMonitoringApproachReportingEmissions';
import { ReportableAndBiomassEmission } from './reportableAndBiomassEmission';

export interface MeasurementOfCO2ReportingEmissions extends DreMonitoringApproachReportingEmissions {
  emissions: ReportableAndBiomassEmission;
  measureTransferredCO2?: boolean;
  transferredCO2Emissions?: ReportableAndBiomassEmission;
}
