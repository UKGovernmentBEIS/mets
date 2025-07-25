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

export interface VerificationBodyDTO {
  id?: number;
  name?: string;
  accreditationReferenceNumber?: string;
  status?: 'ACTIVE' | 'PENDING' | 'DISABLED';
  address?: AddressDTO;
  emissionTradingSchemes?: Array<'UK_ETS_INSTALLATIONS' | 'EU_ETS_INSTALLATIONS' | 'UK_ETS_AVIATION' | 'CORSIA'>;
}
