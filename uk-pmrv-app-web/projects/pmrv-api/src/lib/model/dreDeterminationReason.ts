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

export interface DreDeterminationReason {
  type:
    | 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER'
    | 'CORRECTING_NON_MATERIAL_MISSTATEMENT'
    | 'SURRENDER_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER'
    | 'REVOCATION_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER'
    | 'OTHER';
  typeOtherSummary?: string;
  operatorAskedToResubmit?: boolean;
  regulatorComments?: string;
  supportingDocuments?: Array<string>;
}
