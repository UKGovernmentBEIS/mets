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
import { EmissionsMonitoringPlanCorsia } from './emissionsMonitoringPlanCorsia';
import { ServiceContactDetails } from './serviceContactDetails';

export interface EmissionsMonitoringPlanCorsiaContainer {
  scheme?: 'UK_ETS_INSTALLATIONS' | 'EU_ETS_INSTALLATIONS' | 'UK_ETS_AVIATION' | 'CORSIA';
  empAttachments?: { [key: string]: string };
  emissionsMonitoringPlan: EmissionsMonitoringPlanCorsia;
  serviceContactDetails: ServiceContactDetails;
}
