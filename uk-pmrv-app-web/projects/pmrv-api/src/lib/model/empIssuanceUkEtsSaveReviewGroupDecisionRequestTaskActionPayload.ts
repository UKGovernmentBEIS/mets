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
import { EmpIssuanceReviewDecision } from './empIssuanceReviewDecision';
import { RequestTaskActionPayload } from './requestTaskActionPayload';

export interface EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {
  reviewGroup:
    | 'SERVICE_CONTACT_DETAILS'
    | 'OPERATOR_DETAILS'
    | 'FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES'
    | 'MONITORING_APPROACH'
    | 'EMISSION_SOURCES'
    | 'EMISSIONS_REDUCTION_CLAIM'
    | 'MANAGEMENT_PROCEDURES'
    | 'ABBREVIATIONS_AND_DEFINITIONS'
    | 'ADDITIONAL_DOCUMENTS'
    | 'LATE_SUBMISSION'
    | 'METHOD_A_PROCEDURES'
    | 'METHOD_B_PROCEDURES'
    | 'BLOCK_ON_OFF_PROCEDURES'
    | 'FUEL_UPLIFT_PROCEDURES'
    | 'BLOCK_HOUR_PROCEDURES'
    | 'DATA_GAPS';
  decision: EmpIssuanceReviewDecision;
  reviewSectionsCompleted?: { [key: string]: boolean };
  empSectionsCompleted?: { [key: string]: Array<boolean> };
}
