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

export interface CalculationCarbonContentStandardReferenceSource {
  otherTypeDetails?: string;
  defaultValue?: string;
  type:
    | 'MONITORING_REPORTING_REGULATION_ARTICLE_25_1'
    | 'MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION2_TABLE_4'
    | 'UK_STEEL_CARBON_IN_SCRAP_AND_ALLOYS_PROTOCOL_LATEST_VERSION'
    | 'LABORATORY_ANALYSIS'
    | 'SUPPLIER_ANALYSIS_DATA'
    | 'PAST_ANALYSIS'
    | 'IN_HOUSE_CALCULATION'
    | 'OTHER';
}