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
import { AviationRptAirportsDTO } from './aviationRptAirportsDTO';

export interface AerodromePairsTotalEmissions {
  departureAirport: AviationRptAirportsDTO;
  arrivalAirport: AviationRptAirportsDTO;
  flightsNumber: number;
  emissions: string;
}