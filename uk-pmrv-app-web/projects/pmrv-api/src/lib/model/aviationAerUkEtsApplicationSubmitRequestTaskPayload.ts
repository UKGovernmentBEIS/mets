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
import { AviationAerMonitoringPlanVersion } from './aviationAerMonitoringPlanVersion';
import { AviationAerReportingObligationDetails } from './aviationAerReportingObligationDetails';
import { AviationAerUkEts } from './aviationAerUkEts';
import { EmpUkEtsOriginatedData } from './empUkEtsOriginatedData';
import { RequestTaskPayload } from './requestTaskPayload';
import { ServiceContactDetails } from './serviceContactDetails';

export interface AviationAerUkEtsApplicationSubmitRequestTaskPayload extends RequestTaskPayload {
  reportingYear?: number;
  serviceContactDetails?: ServiceContactDetails;
  reportingRequired?: boolean;
  reportingObligationDetails?: AviationAerReportingObligationDetails;
  aerMonitoringPlanVersions?: Array<AviationAerMonitoringPlanVersion>;
  aerSectionsCompleted?: { [key: string]: Array<boolean> };
  aerAttachments?: { [key: string]: string };
  verificationSectionsCompleted?: { [key: string]: Array<boolean> };
  aer?: AviationAerUkEts;
  empOriginatedData?: EmpUkEtsOriginatedData;
  verificationPerformed?: boolean;
  verificationBodyId?: number;
}