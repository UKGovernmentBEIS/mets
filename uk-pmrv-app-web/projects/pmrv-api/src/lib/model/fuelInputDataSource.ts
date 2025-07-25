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

export interface FuelInputDataSource {
  dataSourceNumber: string;
  fuelInput?:
    | 'METHOD_MONITORING_PLAN'
    | 'LEGAL_METROLOGICAL_CONTROL'
    | 'OPERATOR_CONTROL_NOT_POINT_B'
    | 'NOT_OPERATOR_CONTROL_NOT_POINT_B'
    | 'INDIRECT_DETERMINATION'
    | 'OTHER_METHODS';
  energyContent?:
    | 'CALCULATION_METHOD_MONITORING_PLAN'
    | 'LABORATORY_ANALYSES_SECTION_61'
    | 'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62'
    | 'CONSTANT_VALUES_STANDARD_SUPPLIER'
    | 'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE';
}
