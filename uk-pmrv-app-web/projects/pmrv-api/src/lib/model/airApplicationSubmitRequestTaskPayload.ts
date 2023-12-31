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
import { AirImprovement } from './airImprovement';
import { OperatorAirImprovementResponse } from './operatorAirImprovementResponse';
import { RequestTaskPayload } from './requestTaskPayload';

export interface AirApplicationSubmitRequestTaskPayload extends RequestTaskPayload {
  airImprovements: { [key: string]: AirImprovement };
  operatorImprovementResponses: { [key: string]: OperatorAirImprovementResponse };
  airSectionsCompleted?: { [key: string]: boolean };
  airAttachments?: { [key: string]: string };
}
