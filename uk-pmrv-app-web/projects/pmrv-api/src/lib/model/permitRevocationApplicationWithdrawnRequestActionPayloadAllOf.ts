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
import { FileInfoDTO } from './fileInfoDTO';
import { RequestActionUserInfo } from './requestActionUserInfo';

export interface PermitRevocationApplicationWithdrawnRequestActionPayloadAllOf {
  reason?: string;
  withdrawFiles?: Array<string>;
  revocationAttachments?: { [key: string]: string };
  decisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  withdrawnOfficialNotice?: FileInfoDTO;
}