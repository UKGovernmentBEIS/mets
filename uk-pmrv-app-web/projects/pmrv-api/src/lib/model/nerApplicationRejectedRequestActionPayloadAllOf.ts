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
import { DecisionNotification } from './decisionNotification';
import { RejectAuthorityResponse } from './rejectAuthorityResponse';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface NerApplicationRejectedRequestActionPayloadAllOf {
  submittedToAuthorityDate?: string;
  authorityResponse?: RejectAuthorityResponse;
  nerAttachments?: { [key: string]: string };
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
}