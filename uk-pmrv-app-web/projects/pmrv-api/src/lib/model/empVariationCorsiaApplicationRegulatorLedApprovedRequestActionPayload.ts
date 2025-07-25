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
import { EmissionsMonitoringPlanCorsia } from './emissionsMonitoringPlanCorsia';
import { EmissionsMonitoringPlanCorsiaContainer } from './emissionsMonitoringPlanCorsiaContainer';
import { EmpAcceptedVariationDecisionDetails } from './empAcceptedVariationDecisionDetails';
import { EmpVariationCorsiaDetails } from './empVariationCorsiaDetails';
import { FileInfoDTO } from './fileInfoDTO';
import { RequestActionPayload } from './requestActionPayload';
import { RequestActionUserInfo } from './requestActionUserInfo';
import { ServiceContactDetails } from './serviceContactDetails';

export interface EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload extends RequestActionPayload {
  emissionsMonitoringPlan: EmissionsMonitoringPlanCorsia;
  serviceContactDetails: ServiceContactDetails;
  empVariationDetails: EmpVariationCorsiaDetails;
  empSectionsCompleted?: { [key: string]: Array<boolean> };
  empAttachments?: { [key: string]: string };
  originalEmpContainer: EmissionsMonitoringPlanCorsiaContainer;
  reviewGroupDecisions?: { [key: string]: EmpAcceptedVariationDecisionDetails };
  reasonRegulatorLed: string;
  decisionNotification: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice: FileInfoDTO;
  empDocument: FileInfoDTO;
}
