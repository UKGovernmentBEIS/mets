package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

/**
 * Request Action Types.
 */
public enum RequestActionType {

    // Installation Account relevant request actions
    INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
    INSTALLATION_ACCOUNT_OPENING_ACCEPTED,
    INSTALLATION_ACCOUNT_OPENING_REJECTED,
    INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,

    // Permit Issuance relevant action types
    PERMIT_ISSUANCE_APPLICATION_SUBMITTED,
    PERMIT_ISSUANCE_APPLICATION_GRANTED,
    PERMIT_ISSUANCE_APPLICATION_REJECTED,
    PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN,
    PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED,
    PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS,
    PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_ISSUANCE_RECALLED_FROM_AMENDS,
    PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED,

    // permit surrender
    PERMIT_SURRENDER_APPLICATION_SUBMITTED,
    PERMIT_SURRENDER_APPLICATION_GRANTED,
    PERMIT_SURRENDER_APPLICATION_REJECTED,
    PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN,
    PERMIT_SURRENDER_APPLICATION_CANCELLED,
    PERMIT_SURRENDER_PEER_REVIEW_REQUESTED,
    PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_SURRENDER_CESSATION_COMPLETED,

    // Permit notification
    PERMIT_NOTIFICATION_APPLICATION_SUBMITTED,
    PERMIT_NOTIFICATION_APPLICATION_GRANTED,
    PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED,
    PERMIT_NOTIFICATION_APPLICATION_REJECTED,
    PERMIT_NOTIFICATION_APPLICATION_CANCELLED,
    PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED,
    PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED,
    PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED,

    PERMIT_NOTIFICATION_APPLICATION_COMPLETED,
    PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS,
    PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS,
    PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED,

    // permit revocation
    PERMIT_REVOCATION_APPLICATION_SUBMITTED,
    PERMIT_REVOCATION_APPLICATION_CANCELLED,
    PERMIT_REVOCATION_PEER_REVIEW_REQUESTED,
    PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_REVOCATION_APPLICATION_WITHDRAWN,
    PERMIT_REVOCATION_CESSATION_COMPLETED,
    
    // permit variation
    PERMIT_VARIATION_APPLICATION_SUBMITTED,
    PERMIT_VARIATION_APPLICATION_CANCELLED,
    PERMIT_VARIATION_APPLICATION_GRANTED,
    PERMIT_VARIATION_APPLICATION_REJECTED,
    PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN,
    PERMIT_VARIATION_PEER_REVIEW_REQUESTED,
    PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS,
    PERMIT_VARIATION_APPLICATION_AMENDS_SUBMITTED,
    PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED,
    PERMIT_VARIATION_RECALLED_FROM_AMENDS,
    
    // permit transfer
    PERMIT_TRANSFER_A_APPLICATION_SUBMITTED,
    PERMIT_TRANSFER_A_APPLICATION_GRANTED,
    PERMIT_TRANSFER_A_APPLICATION_REJECTED,
    PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN,
    PERMIT_TRANSFER_B_APPLICATION_SUBMITTED,
    PERMIT_TRANSFER_B_APPLICATION_GRANTED,
    PERMIT_TRANSFER_B_APPLICATION_REJECTED,
    PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN,
    PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED,
    PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_ACCEPTED,
    PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_REJECTED,
    PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS,
    PERMIT_TRANSFER_B_RECALLED_FROM_AMENDS,
    PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED,
    
    PERMIT_TRANSFER_APPLICATION_CANCELLED,
    
    // non compliance
    NON_COMPLIANCE_APPLICATION_CLOSED,
    NON_COMPLIANCE_APPLICATION_SUBMITTED,
    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED,
    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED,
    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED,
    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED,
    NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED,
    NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED,
    NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED,
    NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED,
    NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED,
    NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_REQUESTED,
    NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_ACCEPTED,
    NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_REJECTED,
    NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED,
    
    // NER
    NER_APPLICATION_CANCELLED,
    NER_APPLICATION_SUBMITTED,
    NER_PEER_REVIEW_REQUESTED,
    NER_PEER_REVIEWER_ACCEPTED,
    NER_PEER_REVIEWER_REJECTED,
    NER_APPLICATION_RETURNED_FOR_AMENDS,
    NER_APPLICATION_AMENDS_SUBMITTED,
    NER_APPLICATION_CLOSED,
    NER_APPLICATION_DEEMED_WITHDRAWN,
    NER_APPLICATION_PROCEEDED_TO_AUTHORITY,
    NER_APPLICATION_ACCEPTED,
    NER_APPLICATION_ACCEPTED_WITH_CORRECTIONS,
    NER_APPLICATION_REJECTED,

    // DOAL
    DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY,
    DOAL_APPLICATION_PEER_REVIEW_REQUESTED,
    DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED,
    DOAL_APPLICATION_PEER_REVIEWER_REJECTED,
    DOAL_APPLICATION_CLOSED,
    DOAL_APPLICATION_CANCELLED,
    DOAL_APPLICATION_ACCEPTED,
    DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS,
    DOAL_APPLICATION_REJECTED,
    
    // AER
    AER_APPLICATION_SUBMITTED,
    AER_APPLICATION_SENT_TO_VERIFIER,
    AER_RECALLED_FROM_VERIFICATION,
    AER_APPLICATION_REVIEW_SKIPPED,
    AER_APPLICATION_COMPLETED,
    AER_APPLICATION_VERIFICATION_SUBMITTED,
    AER_APPLICATION_RE_INITIATED,
    AER_APPLICATION_RETURNED_FOR_AMENDS,
    AER_APPLICATION_AMENDS_SUBMITTED,
    AER_APPLICATION_AMENDS_SENT_TO_VERIFIER,
    AER_APPLICATION_NOT_REQUIRED,
    AER_VERIFICATION_RETURNED_TO_OPERATOR,

    // DRE
    DRE_APPLICATION_SUBMITTED,
    DRE_APPLICATION_CANCELLED,
    DRE_APPLICATION_PEER_REVIEW_REQUESTED,
    DRE_APPLICATION_PEER_REVIEWER_ACCEPTED,
    DRE_APPLICATION_PEER_REVIEWER_REJECTED,

    // VIR
    VIR_APPLICATION_SUBMITTED,
    VIR_APPLICATION_REVIEWED,
    VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,
    
    // AIR
    AIR_APPLICATION_SUBMITTED,
    AIR_APPLICATION_REVIEWED,
    AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,
    
    // batch reissue
    BATCH_REISSUE_SUBMITTED,
    BATCH_REISSUE_COMPLETED,
    REISSUE_COMPLETED,

    // rfi
    RFI_SUBMITTED,
    RFI_CANCELLED,
    RFI_EXPIRED,
    RFI_RESPONSE_SUBMITTED,

    // Request for Determination Extension (RDE)
    RDE_SUBMITTED,
    RDE_ACCEPTED,
    RDE_REJECTED,
    RDE_FORCE_ACCEPTED,
    RDE_FORCE_REJECTED,
    RDE_EXPIRED,
    RDE_CANCELLED,

    //payment related request actions
    PAYMENT_MARKED_AS_PAID,
    PAYMENT_MARKED_AS_RECEIVED,
    PAYMENT_COMPLETED,
    PAYMENT_CANCELLED,

    WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED,
    WITHHOLDING_OF_ALLOWANCES_APPLICATION_CANCELLED,

    WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
    WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED,
    WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED,

    WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN,
    WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED,

    RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED,
    RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED,
    RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
    RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED,
    RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED,
    RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED,

    //installation inspection
    INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED,
    INSTALLATION_AUDIT_APPLICATION_SUBMITTED,

    INSTALLATION_ONSITE_INSPECTION_APPLICATION_CANCELLED,
    INSTALLATION_AUDIT_APPLICATION_CANCELLED,

    INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW_REQUESTED,
    INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_REQUESTED,

    INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED,
    INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_ACCEPTED,

    INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_REJECTED,
    INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_REJECTED,

    //bdr
    BDR_APPLICATION_SENT_TO_VERIFIER,
    BDR_RECALLED_FROM_VERIFICATION,
    BDR_APPLICATION_SENT_TO_REGULATOR,
    BDR_VERIFICATION_RETURNED_TO_OPERATOR,
    BDR_APPLICATION_VERIFICATION_SUBMITTED,
    BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
    BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER,
    BDR_APPLICATION_COMPLETED,
    BDR_APPLICATION_PEER_REVIEW_REQUESTED,
    BDR_APPLICATION_PEER_REVIEW_ACCEPTED,
    BDR_APPLICATION_PEER_REVIEW_REJECTED,
    BDR_APPLICATION_RE_INITIATED,

    //Permanent Cessation
    PERMANENT_CESSATION_APPLICATION_CANCELLED,
    PERMANENT_CESSATION_APPLICATION_SUBMITTED,
    PERMANENT_CESSATION_SUBMITTED,
    PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REQUESTED,
    PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_ACCEPTED,
    PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REJECTED,

    ALR_APPLICATION_SENT_TO_VERIFIER,
    ALR_RECALLED_FROM_VERIFICATION,
    ALR_VERIFICATION_RETURNED_TO_OPERATOR,
    ALR_APPLICATION_VERIFICATION_SUBMITTED,


    INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED,
    INSTALLATION_AUDIT_OPERATOR_RESPONDED,

    // common action type for requests terminated by the system
    REQUEST_TERMINATED,
    VERIFICATION_STATEMENT_CANCELLED,

    //EMP UKETS
    EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED,
    EMP_ISSUANCE_UKETS_APPLICATION_APPROVED,
    EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN,
    EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED,
    EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED,
    EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED,
    EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS,
    EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED,
    EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS,
    
    // EMP Variation
    EMP_VARIATION_UKETS_APPLICATION_SUBMITTED,
    EMP_VARIATION_APPLICATION_CANCELLED,
    EMP_VARIATION_UKETS_APPLICATION_APPROVED,
    EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED,
    EMP_VARIATION_UKETS_APPLICATION_REJECTED,
    EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN,
    EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED,
    EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED,
    EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED,
    EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS,
    EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED,
    EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS,

    //Aviation AER UKETS
    AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER,
    AVIATION_AER_UKETS_APPLICATION_SUBMITTED,
    AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED,
    AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE,
    AVIATION_AER_UKETS_APPLICATION_COMPLETED,
    AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED,
    AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS,
    AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED,
    AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER,
    AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR,

    AVIATION_AER_RECALLED_FROM_VERIFICATION, //can be reused both for UK ETS and CORSIA flows
    AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT,
    AVIATION_AER_APPLICATION_RE_INITIATED,

    // aviation account closure
    AVIATION_ACCOUNT_CLOSURE_SUBMITTED,
    AVIATION_ACCOUNT_CLOSURE_CANCELLED,

    // Aviation DRE
    AVIATION_DRE_APPLICATION_CANCELLED,
    AVIATION_DRE_UKETS_APPLICATION_SUBMITTED,
    AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED,
    AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED,
    AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED,

    //Aviation DOE CORSIA
    AVIATION_DOE_CORSIA_SUBMIT_CANCELLED,
    AVIATION_DOE_CORSIA_SUBMITTED,
    AVIATION_DOE_CORSIA_PEER_REVIEW_REQUESTED,
    AVIATION_DOE_CORSIA_PEER_REVIEWER_ACCEPTED,
    AVIATION_DOE_CORSIA_PEER_REVIEWER_REJECTED,

    // Aviation VIR
    AVIATION_VIR_APPLICATION_SUBMITTED,
    AVIATION_VIR_APPLICATION_REVIEWED,
    AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,

    //Aviation EMP CORSIA Issuance
    EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED,
    EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED,
    EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN,
    EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED,
    EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED,
    EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED,
    EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS,
    EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED,
    EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS,

    //Aviation EMP CORSIA Variation
    EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED,
    EMP_VARIATION_CORSIA_APPLICATION_APPROVED,
    EMP_VARIATION_CORSIA_APPLICATION_REJECTED,
    EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN,
    EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED,
    EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED,
    EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED,
    EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED,
    EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS,
    EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED,
    EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS,

    //Aviation AER CORSIA
    AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER,
    AVIATION_AER_CORSIA_APPLICATION_SUBMITTED,
    AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED,
    AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED,
    AVIATION_AER_CORSIA_APPLICATION_CANCELLED_DUE_TO_DOE,
    AVIATION_AER_CORSIA_APPLICATION_COMPLETED,
    AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS,
    AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER,
    AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED,
    AVIATION_AER_CORSIA_VERIFICATION_RETURNED_TO_OPERATOR,

    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_CANCELLED,
    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED,

    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED,
    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED,
    AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED,

    AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_CANCELLED,
    AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED,
    AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED,
    AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED,
    AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED
}
