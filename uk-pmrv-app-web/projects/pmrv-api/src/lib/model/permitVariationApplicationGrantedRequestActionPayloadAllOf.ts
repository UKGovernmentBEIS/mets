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
import { InstallationOperatorDetails } from './installationOperatorDetails';
import { Permit } from './permit';
import { PermitContainer } from './permitContainer';
import { PermitVariationDetails } from './permitVariationDetails';
import { PermitVariationGrantDetermination } from './permitVariationGrantDetermination';
import { PermitVariationReviewDecision } from './permitVariationReviewDecision';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface PermitVariationApplicationGrantedRequestActionPayloadAllOf {
  permitType?: 'GHGE' | 'HSE' | 'WASTE';
  installationOperatorDetails?: InstallationOperatorDetails;
  permit?: Permit;
  permitVariationDetails?: PermitVariationDetails;
  permitAttachments?: { [key: string]: string };
  permitSectionsCompleted?: { [key: string]: Array<boolean> };
  originalPermitContainer?: PermitContainer;
  reviewGroupDecisions?: { [key: string]: PermitVariationReviewDecision };
  permitVariationDetailsReviewDecision?: PermitVariationReviewDecision;
  reviewAttachments?: { [key: string]: string };
  determination?: PermitVariationGrantDetermination;
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice?: FileInfoDTO;
  permitDocument?: FileInfoDTO;
}
