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

export interface PermitVariationRegulatorLedGrantDetermination {
  reason: string;
  activationDate: string;
  annualEmissionsTargets?: { [key: string]: string };
  logChanges: string;
  reasonTemplate:
    | 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS'
    | 'FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR'
    | 'HSE_SITUATIONS_RESPONSE'
    | 'PERMIT_FORMAL_REVIEW_RESPONSE'
    | 'VARIATION_POWERS_FREE_ALLOCATION_PROVISIONS'
    | 'OTHER';
  reasonTemplateOtherSummary?: string;
}