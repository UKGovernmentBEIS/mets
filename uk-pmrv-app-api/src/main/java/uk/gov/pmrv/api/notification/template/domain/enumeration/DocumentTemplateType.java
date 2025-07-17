package uk.gov.pmrv.api.notification.template.domain.enumeration;

public enum DocumentTemplateType {

    /** INExtension_RequestRFI_#L025 */
    IN_RFI,
    
    /** INExtension_RequestRDE_#L026 */
    IN_RDE,

    /** INPermitApplication_L013 */
    PERMIT_ISSUANCE_GHGE_ACCEPTED,

    /** INPermitApplication_L014 */
    PERMIT_ISSUANCE_HSE_ACCEPTED,

    PERMIT_ISSUANCE_WASTE_ACCEPTED,

    /** INPermitApplication_L015 */
    PERMIT_ISSUANCE_REJECTED,

    /** INPermitApplication_L017 */
    PERMIT_ISSUANCE_DEEMED_WITHDRAWN,
    
    /** L028_Surrendered */
    PERMIT_SURRENDERED_NOTICE,
    
    /** L031_SurrenderRefused */
    PERMIT_SURRENDER_REJECTED_NOTICE,
    
    /** L132_SurrenderDeemedWithdrawn */
    PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE,
    
    /** L022_CessationFollowingSurrender */
    PERMIT_SURRENDER_CESSATION,
    
    /** L020_P3_Revocation */
    PERMIT_REVOCATION,
    
    /** L021_P3_Revocation_Withdrawn */
    PERMIT_REVOCATION_WITHDRAWN,
    
    /** L022_P3_Cessation */
    PERMIT_REVOCATION_CESSATION,

    /** L010_IN_Notification_Accepted */
    PERMIT_NOTIFICATION_ACCEPTED,

    /** L011_IN_Notification_Refused */
    PERMIT_NOTIFICATION_REFUSED,
    
    /** L044_VariationApproved */
    PERMIT_VARIATION_ACCEPTED,
    
    /** L045_CAInitiatedVariationApproved */
    PERMIT_VARIATION_REGULATOR_LED_APPROVED,
    
    /** L040_Refused */
    PERMIT_VARIATION_REJECTED,
    
    /** L041_DeemedWithdrawn */
    PERMIT_VARIATION_DEEMED_WITHDRAWN,

    /** L037_INTransfer_Approved **/
    PERMIT_TRANSFER_ACCEPTED,

    /** L036_INTransfer_Refused **/
    PERMIT_TRANSFER_REFUSED,
    
    /** L034_INTransfer_DeemedWithdrawn **/
    PERMIT_TRANSFER_DEEMED_WITHDRAWN,
    
    /** INBatchReissue **/
    PERMIT_REISSUE,

    /** INPermitApplication_Permit */
    PERMIT,

    /**   IN Permanent Cessation - Template - EA.docx */
    PERMANENT_CESSATION,
    PERMANENT_CESSATION_APPLICATION_SUBMIT,
    PERMANENT_CESSATION_APPLICATION_PEER_REVIEW,

    /** DOAL Preliminary Allocation Letter Final v01.doc */
    DOAL_SUBMITTED,
    /** Notice of DOAL Approval of Allocation (with corrections) Article 34H & 6a Final v01.doc */
    DOAL_ACCEPTED,
    /** Notice of DOAL Rejection of Allocation Article 34H & 6a Final v01.doc */
    DOAL_REJECTED,
    
    DRE_SUBMITTED,


    /** INP3ImprovementReport_VerifierImprovements_#L003 */
    VIR_REVIEWED,

    /** INP3ImprovementReport_AnnualImprovements_#L002 */
    AIR_REVIEWED,

    WITHHOLDING_OF_ALLOWANCES,

    WITHHOLDING_OF_ALLOWANCES_WITHDRAWN,

    RETURN_OF_ALLOWANCES,

    INSTALLATION_ONSITE_INSPECTION_SUBMITTED,
    INSTALLATION_AUDIT_SUBMITTED,

    AVIATION_DRE_SUBMITTED,

    AVIATION_DOE_SUBMITTED,

    AVIATION_VIR_REVIEWED,

    /** UK_ETS_monitoring_plan_template_aviation_MR */
    EMP_UKETS,

    /** UK_ETS_EMP Approval Notice_METS */
    EMP_ISSUANCE_UKETS_GRANTED,

    /** UK_ETS_EMP Withdrawn Notice_METS */
    EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN,
    
    /** UK ETS_EMP Variation Approve Notice_METS */
    EMP_VARIATION_UKETS_ACCEPTED,
    
    /** UK ETS_EMP Variation Refusal Notice_METS */
    EMP_VARIATION_UKETS_REJECTED,

    /** UK ETS_EMP Variation Withdrawn_Notice_METS */
    EMP_VARIATION_UKETS_DEEMED_WITHDRAWN,

    /** CA_Initiated_AEMVariation_Approved_L061 */
    EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED,

    EMP_REISSUE_UKETS,

    /** CORSIA_monitoring_plan_template_aviation_MR */
    EMP_CORSIA,

    /** CORSIA_EMP Approval Notice_METS */
    EMP_ISSUANCE_CORSIA_GRANTED,

    /** CORSIA_EMP Withdrawn Notice_METS */
    EMP_ISSUANCE_CORSIA_DEEMED_WITHDRAWN,

    EMP_REISSUE_CORSIA,

    EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED,

    /** CORSIA_EMP Variation Approve Notice_METS */
    EMP_VARIATION_CORSIA_ACCEPTED,

    /** CORSIA_EMP Variation Refusal Notice_METS */
    EMP_VARIATION_CORSIA_REJECTED,

    /** CORSIA_EMP Variation Withdrawn_Notice_METS */
    EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN,

    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED,
    AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED
}
