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
import { InstallationOperatorDetails } from './installationOperatorDetails';
import { Permit } from './permit';
import { PermitAcceptedVariationDecisionDetails } from './permitAcceptedVariationDecisionDetails';
import { PermitContainer } from './permitContainer';
import { PermitVariationDetails } from './permitVariationDetails';
import { PermitVariationRegulatorLedGrantDetermination } from './permitVariationRegulatorLedGrantDetermination';
import { RequestTaskPayload } from './requestTaskPayload';

export interface PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload extends RequestTaskPayload {
  permitType?: 'GHGE' | 'HSE' | 'WASTE';
  permit?: Permit;
  installationOperatorDetails?: InstallationOperatorDetails;
  permitVariationDetails?: PermitVariationDetails;
  permitVariationDetailsCompleted?: boolean;
  permitSectionsCompleted?: { [key: string]: Array<boolean> };
  permitAttachments?: { [key: string]: string };
  originalPermitContainer?: PermitContainer;
  permitVariationDetailsReviewDecision?: PermitAcceptedVariationDecisionDetails;
  reviewGroupDecisions?: { [key: string]: PermitAcceptedVariationDecisionDetails };
  determination?: PermitVariationRegulatorLedGrantDetermination;
  reviewSectionsCompleted?: { [key: string]: boolean };
}
