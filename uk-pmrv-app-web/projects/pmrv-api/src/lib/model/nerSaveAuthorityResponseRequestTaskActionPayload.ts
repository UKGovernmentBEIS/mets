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
import { GrantAuthorityResponse } from './grantAuthorityResponse';
import { RejectAuthorityResponse } from './rejectAuthorityResponse';
import { RequestTaskActionPayload } from './requestTaskActionPayload';

export interface NerSaveAuthorityResponseRequestTaskActionPayload extends RequestTaskActionPayload {
  submittedToAuthorityDate?: string;
  authorityResponse?: GrantAuthorityResponse | RejectAuthorityResponse;
  nerSectionsCompleted?: { [key: string]: boolean };
}
