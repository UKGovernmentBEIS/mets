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
import { TransferCO2 } from './transferCO2';

export interface CalculationSourceStreamCategory {
  sourceStream: string;
  emissionSources: Array<string>;
  annualEmittedCO2Tonnes: string;
  categoryType: 'MAJOR' | 'MINOR' | 'DE_MINIMIS' | 'MARGINAL';
  calculationMethod: 'STANDARD' | 'MASS_BALANCE';
  transfer?: TransferCO2;
}