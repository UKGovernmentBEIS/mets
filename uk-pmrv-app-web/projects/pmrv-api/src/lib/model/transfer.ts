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
import { InstallationDetails } from './installationDetails';
import { InstallationEmitter } from './installationEmitter';

export interface Transfer {
  entryAccountingForTransfer?: boolean;
  installationDetailsType?: 'INSTALLATION_EMITTER' | 'INSTALLATION_DETAILS';
  transferType: 'TRANSFER_CO2' | 'TRANSFER_N2O';
  installationEmitter?: InstallationEmitter;
  installationDetails?: InstallationDetails;
}