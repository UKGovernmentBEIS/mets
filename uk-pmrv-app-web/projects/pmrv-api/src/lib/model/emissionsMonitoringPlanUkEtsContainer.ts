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
import { EmissionsMonitoringPlanUkEts } from './emissionsMonitoringPlanUkEts';
import { ServiceContactDetails } from './serviceContactDetails';

export interface EmissionsMonitoringPlanUkEtsContainer {
  scheme?: 'UK_ETS_INSTALLATIONS' | 'EU_ETS_INSTALLATIONS' | 'UK_ETS_AVIATION' | 'CORSIA';
  empAttachments?: { [key: string]: string };
  emissionsMonitoringPlan: EmissionsMonitoringPlanUkEts;
  serviceContactDetails: ServiceContactDetails;
}