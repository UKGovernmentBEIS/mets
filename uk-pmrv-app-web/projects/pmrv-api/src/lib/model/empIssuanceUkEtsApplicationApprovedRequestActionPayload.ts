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
import { EmissionsMonitoringPlanUkEts } from './emissionsMonitoringPlanUkEts';
import { EmpIssuanceDetermination } from './empIssuanceDetermination';
import { EmpIssuanceReviewDecision } from './empIssuanceReviewDecision';
import { FileInfoDTO } from './fileInfoDTO';
import { RequestActionPayload } from './requestActionPayload';
import { RequestActionUserInfo } from './requestActionUserInfo';
import { ServiceContactDetails } from './serviceContactDetails';

export interface EmpIssuanceUkEtsApplicationApprovedRequestActionPayload extends RequestActionPayload {
  emissionsMonitoringPlan: EmissionsMonitoringPlanUkEts;
  serviceContactDetails: ServiceContactDetails;
  empSectionsCompleted?: { [key: string]: Array<boolean> };
  empAttachments?: { [key: string]: string };
  decisionNotification: DecisionNotification;
  reviewGroupDecisions?: { [key: string]: EmpIssuanceReviewDecision };
  reviewAttachments?: { [key: string]: string };
  determination: EmpIssuanceDetermination;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice: FileInfoDTO;
  empDocument: FileInfoDTO;
}