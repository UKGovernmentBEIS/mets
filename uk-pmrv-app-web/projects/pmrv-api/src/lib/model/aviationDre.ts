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
import { AviationDreDeterminationReason } from './aviationDreDeterminationReason';
import { AviationDreEmissionsCalculationApproach } from './aviationDreEmissionsCalculationApproach';
import { AviationDreFee } from './aviationDreFee';

export interface AviationDre {
  determinationReason: AviationDreDeterminationReason;
  totalReportableEmissions: string;
  calculationApproach: AviationDreEmissionsCalculationApproach;
  supportingDocuments?: Array<string>;
  fee: AviationDreFee;
}