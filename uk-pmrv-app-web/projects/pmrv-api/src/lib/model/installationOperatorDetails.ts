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
import { AddressDTO } from './addressDTO';
import { LocationDTO } from './locationDTO';
import { LocationOffShoreDTO } from './locationOffShoreDTO';
import { LocationOnShoreDTO } from './locationOnShoreDTO';
import { LocationOnShoreStateDTO } from './locationOnShoreStateDTO';

export interface InstallationOperatorDetails {
  installationName?: string;
  siteName?: string;
  installationLocation?: LocationDTO | LocationOffShoreDTO | LocationOnShoreDTO | LocationOnShoreStateDTO;
  operator?: string;
  operatorType?: 'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'OTHER';
  companyReferenceNumber?: string;
  operatorDetailsAddress?: AddressDTO;
  emitterId?: string;
}
