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
import { FallbackBiomass } from './fallbackBiomass';

export interface FallbackEmissionsAllOf {
  sourceStreams?: Array<string>;
  biomass?: FallbackBiomass;
  description?: string;
  files?: Array<string>;
  totalFossilEmissions?: string;
  totalFossilEnergyContent?: string;
  reportableEmissions?: string;
}