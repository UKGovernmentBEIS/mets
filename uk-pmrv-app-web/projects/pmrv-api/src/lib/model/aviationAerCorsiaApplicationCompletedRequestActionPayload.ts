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
import { AerReviewDecision } from './aerReviewDecision';
import { AviationAerCorsia } from './aviationAerCorsia';
import { AviationAerCorsiaVerificationReport } from './aviationAerCorsiaVerificationReport';
import { AviationAerMonitoringPlanVersion } from './aviationAerMonitoringPlanVersion';
import { AviationAerReportingObligationDetails } from './aviationAerReportingObligationDetails';
import { RequestActionPayload } from './requestActionPayload';
import { ServiceContactDetails } from './serviceContactDetails';

export interface AviationAerCorsiaApplicationCompletedRequestActionPayload extends RequestActionPayload {
  reportingRequired?: boolean;
  reportingObligationDetails?: AviationAerReportingObligationDetails;
  aer?: AviationAerCorsia;
  reportingYear?: number;
  serviceContactDetails?: ServiceContactDetails;
  aerMonitoringPlanVersions?: Array<AviationAerMonitoringPlanVersion>;
  aerAttachments?: { [key: string]: string };
  verificationReport?: AviationAerCorsiaVerificationReport;
  reviewGroupDecisions?: { [key: string]: AerReviewDecision };
  reviewAttachments?: { [key: string]: string };
  verificationAttachments?: { [key: string]: string };
}
