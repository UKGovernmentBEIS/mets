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
import { OperatorAirImprovementFollowUpResponse } from './operatorAirImprovementFollowUpResponse';
import { RequestTaskActionPayload } from './requestTaskActionPayload';

export interface AirSaveRespondToRegulatorCommentsRequestTaskActionPayload extends RequestTaskActionPayload {
  reference: number;
  operatorImprovementFollowUpResponse: OperatorAirImprovementFollowUpResponse;
  airRespondToRegulatorCommentsSectionsCompleted?: { [key: string]: boolean };
}
