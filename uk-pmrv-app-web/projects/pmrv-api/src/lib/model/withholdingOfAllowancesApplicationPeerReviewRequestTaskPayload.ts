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
import { RequestTaskPayload } from './requestTaskPayload';
import { WithholdingOfAllowances } from './withholdingOfAllowances';

export interface WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload extends RequestTaskPayload {
  withholdingOfAllowances: WithholdingOfAllowances;
  sectionsCompleted?: { [key: string]: boolean };
}
