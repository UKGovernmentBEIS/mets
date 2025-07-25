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
import { PreliminaryAllocation } from './preliminaryAllocation';

export interface GrantAuthorityResponse {
  type: 'VALID' | 'VALID_WITH_CORRECTIONS' | 'INVALID';
  monitoringMethodologyPlanApproved: boolean;
  decisionComments?: string;
  files?: Array<string>;
  preliminaryAllocations?: Array<PreliminaryAllocation>;
  allocationComments: string;
}
