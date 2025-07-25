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
import { OperatorImprovementResponse } from './operatorImprovementResponse';
import { RegulatorReviewResponse } from './regulatorReviewResponse';
import { RequestActionUserInfo } from './requestActionUserInfo';
import { VirVerificationData } from './virVerificationData';

export interface VirApplicationReviewedRequestActionPayloadAllOf {
  reportingYear?: number;
  virAttachments?: { [key: string]: string };
  verificationData?: VirVerificationData;
  operatorImprovementResponses?: { [key: string]: OperatorImprovementResponse };
  regulatorReviewResponse?: RegulatorReviewResponse;
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice?: FileInfoDTO;
}
