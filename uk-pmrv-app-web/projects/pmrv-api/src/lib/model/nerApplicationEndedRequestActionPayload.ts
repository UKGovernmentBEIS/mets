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
import { DecisionNotification } from './decisionNotification';
import { NerEndedDetermination } from './nerEndedDetermination';
import { RequestActionPayload } from './requestActionPayload';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface NerApplicationEndedRequestActionPayload extends RequestActionPayload {
  determination: NerEndedDetermination;
  decisionNotification: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
}
