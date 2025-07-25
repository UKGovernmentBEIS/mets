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
import { DecisionNotification } from './decisionNotification';

/**
 * The request to preview the document
 */
export interface PreviewDocumentRequest {
  documentType:
    | 'IN_RFI'
    | 'IN_RDE'
    | 'PERMIT_ISSUANCE_GHGE_ACCEPTED'
    | 'PERMIT_ISSUANCE_HSE_ACCEPTED'
    | 'PERMIT_ISSUANCE_WASTE_ACCEPTED'
    | 'PERMIT_ISSUANCE_REJECTED'
    | 'PERMIT_ISSUANCE_DEEMED_WITHDRAWN'
    | 'PERMIT_SURRENDERED_NOTICE'
    | 'PERMIT_SURRENDER_REJECTED_NOTICE'
    | 'PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE'
    | 'PERMIT_SURRENDER_CESSATION'
    | 'PERMIT_REVOCATION'
    | 'PERMIT_REVOCATION_WITHDRAWN'
    | 'PERMIT_REVOCATION_CESSATION'
    | 'PERMIT_NOTIFICATION_ACCEPTED'
    | 'PERMIT_NOTIFICATION_REFUSED'
    | 'PERMIT_VARIATION_ACCEPTED'
    | 'PERMIT_VARIATION_REGULATOR_LED_APPROVED'
    | 'PERMIT_VARIATION_REJECTED'
    | 'PERMIT_VARIATION_DEEMED_WITHDRAWN'
    | 'PERMIT_TRANSFER_ACCEPTED'
    | 'PERMIT_TRANSFER_REFUSED'
    | 'PERMIT_TRANSFER_DEEMED_WITHDRAWN'
    | 'PERMIT_REISSUE'
    | 'PERMIT'
    | 'PERMANENT_CESSATION'
    | 'PERMANENT_CESSATION_APPLICATION_SUBMIT'
    | 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW'
    | 'DOAL_SUBMITTED'
    | 'DOAL_ACCEPTED'
    | 'DOAL_REJECTED'
    | 'DRE_SUBMITTED'
    | 'VIR_REVIEWED'
    | 'AIR_REVIEWED'
    | 'WITHHOLDING_OF_ALLOWANCES'
    | 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWN'
    | 'RETURN_OF_ALLOWANCES'
    | 'INSTALLATION_ONSITE_INSPECTION_SUBMITTED'
    | 'INSTALLATION_AUDIT_SUBMITTED'
    | 'AVIATION_DRE_SUBMITTED'
    | 'AVIATION_DOE_SUBMITTED'
    | 'AVIATION_VIR_REVIEWED'
    | 'EMP_UKETS'
    | 'EMP_ISSUANCE_UKETS_GRANTED'
    | 'EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN'
    | 'EMP_VARIATION_UKETS_ACCEPTED'
    | 'EMP_VARIATION_UKETS_REJECTED'
    | 'EMP_VARIATION_UKETS_DEEMED_WITHDRAWN'
    | 'EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED'
    | 'EMP_REISSUE_UKETS'
    | 'EMP_CORSIA'
    | 'EMP_ISSUANCE_CORSIA_GRANTED'
    | 'EMP_ISSUANCE_CORSIA_DEEMED_WITHDRAWN'
    | 'EMP_REISSUE_CORSIA'
    | 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED'
    | 'EMP_VARIATION_CORSIA_ACCEPTED'
    | 'EMP_VARIATION_CORSIA_REJECTED'
    | 'EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN'
    | 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED'
    | 'AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED';
  decisionNotification: DecisionNotification;
}
