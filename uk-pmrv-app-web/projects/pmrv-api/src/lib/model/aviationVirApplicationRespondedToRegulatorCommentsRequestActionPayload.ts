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
import { OperatorImprovementFollowUpResponse } from './operatorImprovementFollowUpResponse';
import { OperatorImprovementResponse } from './operatorImprovementResponse';
import { RegulatorImprovementResponse } from './regulatorImprovementResponse';
import { RequestActionPayload } from './requestActionPayload';
import { UncorrectedItem } from './uncorrectedItem';
import { VerifierComment } from './verifierComment';

export interface AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload extends RequestActionPayload {
  reportingYear: number;
  verifierUncorrectedItem?: UncorrectedItem;
  verifierComment?: VerifierComment;
  operatorImprovementResponse: OperatorImprovementResponse;
  regulatorImprovementResponse: RegulatorImprovementResponse;
  operatorImprovementFollowUpResponse: OperatorImprovementFollowUpResponse;
  virAttachments?: { [key: string]: string };
}