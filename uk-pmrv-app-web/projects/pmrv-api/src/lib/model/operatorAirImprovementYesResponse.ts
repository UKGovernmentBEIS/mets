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
import { OperatorAirImprovementResponse } from './operatorAirImprovementResponse';

export interface OperatorAirImprovementYesResponse extends OperatorAirImprovementResponse {
  proposal: string;
  proposedDate: string;
  files?: Array<string>;
}
