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
import { ActivitiesDescription } from './activitiesDescription';
import { AirOperatingCertificate } from './airOperatingCertificate';
import { FlightIdentification } from './flightIdentification';
import { OperatingLicense } from './operatingLicense';
import { OrganisationStructure } from './organisationStructure';

export interface EmpOperatorDetails {
  operatorName: string;
  crcoCode: string;
  flightIdentification: FlightIdentification;
  airOperatingCertificate: AirOperatingCertificate;
  operatingLicense: OperatingLicense;
  organisationStructure: OrganisationStructure;
  activitiesDescription: ActivitiesDescription;
}
