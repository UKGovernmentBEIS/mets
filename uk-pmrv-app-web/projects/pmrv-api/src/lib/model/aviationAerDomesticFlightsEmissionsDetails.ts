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

export interface AviationAerDomesticFlightsEmissionsDetails {
  flightsNumber: number;
  fuelType: 'JET_KEROSENE' | 'JET_GASOLINE' | 'AVIATION_GASOLINE';
  fuelConsumption: string;
  emissions: string;
  country: string;
}