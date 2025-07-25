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

export interface PermitVariationModification {
  type:
    | 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE'
    | 'INSTALLATION_NAME'
    | 'REGISTERED_OFFICE_ADDRESS'
    | 'INSTALLATION_ADDRESS'
    | 'GRID_REFERENCE'
    | 'METER_RENAMING'
    | 'METER_LOCATION_DESCRIPTION'
    | 'RELEASE_EMISSION_POINT_DESCRIPTION'
    | 'OTHER_NON_SIGNFICANT'
    | 'INSTALLATION_CATEGORY'
    | 'NOTWITHSTANDING_ARTICLE_47_8'
    | 'EMISSION_SOURCES'
    | 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES'
    | 'TIER_APPLIED'
    | 'NEW_SOURCE_STREAMS'
    | 'SOURCE_STREAMS_CATEGORISATION'
    | 'METHODS'
    | 'QUANTIFICATION_METHODOLOGY_FOR_EMISSIONS'
    | 'OTHER_MONITORING_PLAN'
    | 'INSTALLATION_SUB'
    | 'MONITORING_REPORT_METHODOLOGY_4_4_OR_4_6'
    | 'DEFAULT_VALUE_OR_ESTIMATION_METHOD'
    | 'COMPETENT_AUTHORITY'
    | 'OTHER_MONITORING_METHODOLOGY_PLAN';
  otherSummary?: string;
}
