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
import { AdditionalDocuments } from './additionalDocuments';
import { ConfidentialityStatement } from './confidentialityStatement';
import { NerOperatorDocumentWithComment } from './nerOperatorDocumentWithComment';
import { NerReviewGroupDecision } from './nerReviewGroupDecision';
import { RequestTaskPayload } from './requestTaskPayload';

export interface NerApplicationAmendsSubmitRequestTaskPayload extends RequestTaskPayload {
  newEntrantDataReport: NerOperatorDocumentWithComment;
  verifierOpinionStatement: NerOperatorDocumentWithComment;
  monitoringMethodologyPlan: NerOperatorDocumentWithComment;
  confidentialityStatement: ConfidentialityStatement;
  additionalDocuments: AdditionalDocuments;
  nerSectionsCompleted?: { [key: string]: boolean };
  nerAttachments?: { [key: string]: string };
  reviewAttachments?: { [key: string]: string };
  reviewSectionsCompleted?: { [key: string]: boolean };
  reviewGroupDecisions?: { [key: string]: NerReviewGroupDecision };
}