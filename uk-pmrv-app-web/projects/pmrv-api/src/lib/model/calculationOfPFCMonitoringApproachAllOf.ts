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
import { CellAndAnodeType } from './cellAndAnodeType';
import { PFCSourceStreamCategoryAppliedTier } from './pFCSourceStreamCategoryAppliedTier';
import { PFCTier2EmissionFactor } from './pFCTier2EmissionFactor';
import { ProcedureForm } from './procedureForm';

export interface CalculationOfPFCMonitoringApproachAllOf {
  approachDescription?: string;
  cellAndAnodeTypes?: Array<CellAndAnodeType>;
  collectionEfficiency?: ProcedureForm;
  tier2EmissionFactor?: PFCTier2EmissionFactor;
  sourceStreamCategoryAppliedTiers?: Array<PFCSourceStreamCategoryAppliedTier>;
}
