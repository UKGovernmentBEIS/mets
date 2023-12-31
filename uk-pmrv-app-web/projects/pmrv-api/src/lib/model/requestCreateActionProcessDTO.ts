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
import { BatchReissueRequestCreateActionPayload } from './batchReissueRequestCreateActionPayload';
import { DoalRequestCreateActionPayload } from './doalRequestCreateActionPayload';
import { InstallationAccountOpeningSubmitApplicationCreateActionPayload } from './installationAccountOpeningSubmitApplicationCreateActionPayload';
import { ReportRelatedRequestCreateActionPayload } from './reportRelatedRequestCreateActionPayload';
import { RequestCreateActionEmptyPayload } from './requestCreateActionEmptyPayload';

/**
 * The request create action body
 */
export interface RequestCreateActionProcessDTO {
  requestCreateActionType:
    | 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION'
    | 'PERMIT_SURRENDER'
    | 'PERMIT_REVOCATION'
    | 'PERMIT_VARIATION'
    | 'PERMIT_TRANSFER_A'
    | 'PERMIT_NOTIFICATION'
    | 'PERMIT_BATCH_REISSUE'
    | 'NON_COMPLIANCE'
    | 'NER'
    | 'DOAL'
    | 'AER'
    | 'DRE'
    | 'WITHHOLDING_OF_ALLOWANCES'
    | 'RETURN_OF_ALLOWANCES'
    | 'AIR'
    | 'EMP_BATCH_REISSUE'
    | 'AVIATION_ACCOUNT_CLOSURE'
    | 'EMP_VARIATION_UKETS'
    | 'AVIATION_DRE_UKETS'
    | 'AVIATION_NON_COMPLIANCE'
    | 'EMP_VARIATION_CORSIA';
  requestCreateActionPayload:
    | BatchReissueRequestCreateActionPayload
    | DoalRequestCreateActionPayload
    | InstallationAccountOpeningSubmitApplicationCreateActionPayload
    | ReportRelatedRequestCreateActionPayload
    | RequestCreateActionEmptyPayload;
}
