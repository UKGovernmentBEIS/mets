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
import { PermitSurrender } from './permitSurrender';
import { PermitSurrenderReviewDecision } from './permitSurrenderReviewDecision';
import { PermitSurrenderReviewDetermination } from './permitSurrenderReviewDetermination';
import { RequestTaskPayload } from './requestTaskPayload';

export interface PermitSurrenderApplicationReviewRequestTaskPayload extends RequestTaskPayload {
  permitSurrender: PermitSurrender;
  permitSurrenderAttachments?: { [key: string]: string };
  reviewDecision: PermitSurrenderReviewDecision;
  reviewDetermination: PermitSurrenderReviewDetermination;
  reviewDeterminationCompleted?: boolean;
  rfiAttachments?: { [key: string]: string };
}
