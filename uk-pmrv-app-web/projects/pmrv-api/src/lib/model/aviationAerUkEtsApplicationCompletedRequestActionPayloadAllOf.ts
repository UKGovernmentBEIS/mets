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
import { AviationAerUkEtsSubmittedEmissions } from './aviationAerUkEtsSubmittedEmissions';
import { AviationAerUkEtsVerificationReport } from './aviationAerUkEtsVerificationReport';
import { ServiceContactDetails } from './serviceContactDetails';

export interface AviationAerUkEtsApplicationCompletedRequestActionPayloadAllOf {
  reportingRequired?: boolean;
  reportingObligationDetails?: AviationAerReportingObligationDetails;
  aer?: AviationAerUkEts;
  reportingYear?: number;
  serviceContactDetails?: ServiceContactDetails;
  aerMonitoringPlanVersions?: Array<AviationAerMonitoringPlanVersion>;
  verificationPerformed?: boolean;
  submittedEmissions?: AviationAerUkEtsSubmittedEmissions;
  verificationReport?: AviationAerUkEtsVerificationReport;
  aerAttachments?: { [key: string]: string };
  verificationAttachments?: { [key: string]: string };
  reviewGroupDecisions?: { [key: string]: AerReviewDecision };
  reviewAttachments?: { [key: string]: string };
}
