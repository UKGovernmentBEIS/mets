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
import { FallbackSourceStreamCategoryAppliedTier } from './fallbackSourceStreamCategoryAppliedTier';
import { ProcedureForm } from './procedureForm';

export interface FallbackMonitoringApproachAllOf {
  approachDescription?: string;
  justification?: string;
  files?: Array<string>;
  annualUncertaintyAnalysis?: ProcedureForm;
  sourceStreamCategoryAppliedTiers?: Array<FallbackSourceStreamCategoryAppliedTier>;
}