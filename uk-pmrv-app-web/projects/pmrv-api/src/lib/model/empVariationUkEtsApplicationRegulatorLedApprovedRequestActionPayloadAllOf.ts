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
import { EmissionsMonitoringPlanUkEtsContainer } from './emissionsMonitoringPlanUkEtsContainer';
import { EmpAcceptedVariationDecisionDetails } from './empAcceptedVariationDecisionDetails';
import { EmpVariationUkEtsDetails } from './empVariationUkEtsDetails';
import { EmpVariationUkEtsRegulatorLedReason } from './empVariationUkEtsRegulatorLedReason';
import { FileInfoDTO } from './fileInfoDTO';
import { RequestActionUserInfo } from './requestActionUserInfo';
import { ServiceContactDetails } from './serviceContactDetails';

export interface EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayloadAllOf {
  emissionsMonitoringPlan?: EmissionsMonitoringPlanUkEts;
  serviceContactDetails?: ServiceContactDetails;
  empVariationDetails?: EmpVariationUkEtsDetails;
  empSectionsCompleted?: { [key: string]: Array<boolean> };
  empAttachments?: { [key: string]: string };
  originalEmpContainer?: EmissionsMonitoringPlanUkEtsContainer;
  reviewGroupDecisions?: { [key: string]: EmpAcceptedVariationDecisionDetails };
  reasonRegulatorLed?: EmpVariationUkEtsRegulatorLedReason;
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice?: FileInfoDTO;
  empDocument?: FileInfoDTO;
}