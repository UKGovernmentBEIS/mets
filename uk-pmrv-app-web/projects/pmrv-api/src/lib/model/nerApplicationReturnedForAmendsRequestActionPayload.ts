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
import { NerReviewGroupDecision } from './nerReviewGroupDecision';
import { RequestActionPayload } from './requestActionPayload';

export interface NerApplicationReturnedForAmendsRequestActionPayload extends RequestActionPayload {
  reviewGroupDecisions: { [key: string]: NerReviewGroupDecision };
  reviewAttachments?: { [key: string]: string };
}