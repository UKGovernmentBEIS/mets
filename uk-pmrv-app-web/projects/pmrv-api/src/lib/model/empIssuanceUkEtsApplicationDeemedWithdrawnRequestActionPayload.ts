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
import { EmpIssuanceDetermination } from './empIssuanceDetermination';
import { FileInfoDTO } from './fileInfoDTO';
import { RequestActionPayload } from './requestActionPayload';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload extends RequestActionPayload {
  decisionNotification: DecisionNotification;
  determination: EmpIssuanceDetermination;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialNotice: FileInfoDTO;
}