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
import { EmpAcceptedVariationDecisionDetails } from './empAcceptedVariationDecisionDetails';

export interface EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayloadAllOf {
  group?:
    | 'SERVICE_CONTACT_DETAILS'
    | 'OPERATOR_DETAILS'
    | 'FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES'
    | 'MONITORING_APPROACH'
    | 'EMISSION_SOURCES'
    | 'MANAGEMENT_PROCEDURES'
    | 'ABBREVIATIONS_AND_DEFINITIONS'
    | 'ADDITIONAL_DOCUMENTS'
    | 'DATA_GAPS'
    | 'METHOD_A_PROCEDURES'
    | 'METHOD_B_PROCEDURES'
    | 'BLOCK_ON_OFF_PROCEDURES'
    | 'FUEL_UPLIFT_PROCEDURES'
    | 'BLOCK_HOUR_PROCEDURES';
  decision?: EmpAcceptedVariationDecisionDetails;
  empSectionsCompleted?: { [key: string]: Array<boolean> };
}
