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

export interface NotVerifiedReason {
  type:
    | 'UNCORRECTED_MATERIAL_MISSTATEMENT'
    | 'UNCORRECTED_MATERIAL_NON_CONFORMITY'
    | 'DATA_OR_INFORMATION_LIMITATIONS'
    | 'SCOPE_LIMITATIONS_CLARITY'
    | 'SCOPE_LIMITATIONS_MONITORING_PLAN'
    | 'NOT_APPROVED_MONITORING_PLAN'
    | 'ANOTHER_REASON';
  otherReason?: string;
}