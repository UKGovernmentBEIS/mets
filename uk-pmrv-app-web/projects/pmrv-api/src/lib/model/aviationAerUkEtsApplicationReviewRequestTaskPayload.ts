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
import { AerReviewDecision } from './aerReviewDecision';
import { AviationAerMonitoringPlanVersion } from './aviationAerMonitoringPlanVersion';
import { AviationAerReportingObligationDetails } from './aviationAerReportingObligationDetails';
import { AviationAerUkEts } from './aviationAerUkEts';
import { AviationAerUkEtsVerificationReport } from './aviationAerUkEtsVerificationReport';
import { RequestTaskPayload } from './requestTaskPayload';
import { ServiceContactDetails } from './serviceContactDetails';

export interface AviationAerUkEtsApplicationReviewRequestTaskPayload extends RequestTaskPayload {
  reportingYear?: number;
  serviceContactDetails?: ServiceContactDetails;
  reportingRequired?: boolean;
  reportingObligationDetails?: AviationAerReportingObligationDetails;
  aerMonitoringPlanVersions?: Array<AviationAerMonitoringPlanVersion>;
  aerSectionsCompleted?: { [key: string]: Array<boolean> };
  aerAttachments?: { [key: string]: string };
  verificationSectionsCompleted?: { [key: string]: Array<boolean> };
  aer?: AviationAerUkEts;
  verificationReport?: AviationAerUkEtsVerificationReport;
  totalEmissionsProvided?: string;
  notCoveredChangesProvided?: string;
  reviewSectionsCompleted?: { [key: string]: boolean };
  reviewGroupDecisions?: { [key: string]: AerReviewDecision };
  reviewAttachments?: { [key: string]: string };
}
