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
import { FileInfoDTO } from './fileInfoDTO';
import { PermitNotificationReviewDecision } from './permitNotificationReviewDecision';
import { RequestActionPayload } from './requestActionPayload';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload extends RequestActionPayload {
  reviewDecision: PermitNotificationReviewDecision;
  reviewDecisionNotification: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice: FileInfoDTO;
}
