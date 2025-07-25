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
import { PermitNotification } from './permitNotification';

export interface OtherFactor extends PermitNotification {
  reportingType:
    | 'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT'
    | 'EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'
    | 'RENOUNCE_FREE_ALLOCATIONS'
    | 'OTHER_ISSUE';
  startDateOfNonCompliance?: string;
  endDateOfNonCompliance?: string;
}
