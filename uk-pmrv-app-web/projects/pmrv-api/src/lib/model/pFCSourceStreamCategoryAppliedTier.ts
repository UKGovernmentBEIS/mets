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
import { PFCActivityData } from './pFCActivityData';
import { PFCEmissionFactor } from './pFCEmissionFactor';
import { PFCSourceStreamCategory } from './pFCSourceStreamCategory';

export interface PFCSourceStreamCategoryAppliedTier {
  sourceStreamCategory: PFCSourceStreamCategory;
  activityData: PFCActivityData;
  emissionFactor: PFCEmissionFactor;
}
