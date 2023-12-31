package uk.gov.pmrv.api.authorization.core.domain;

public enum Permission {

    // Installation Account Opening Application task
    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
    
    // Installation Account Opening Archive task
    PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK,
    PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK,

    // Permit Issuance Application
    PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK,

    // Permit Surrender Application
    PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
    PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK,

    // Permit Revocation
    PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_REVOCATION_MAKE_PAYMENT_VIEW_TASK,
    PERM_PERMIT_REVOCATION_MAKE_PAYMENT_EXECUTE_TASK,

    // Permit Notification Application
    PERM_PERMIT_NOTIFICATION_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK,

    PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK,
    
    //Permit Variation
    PERM_PERMIT_VARIATION_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_VARIATION_APPLICATION_SUBMIT_EXECUTE_TASK,
    PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
    PERM_PERMIT_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK,
    PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_VARIATION_PEER_REVIEW_EXECUTE_TASK,

    // AER Application
    PERM_AER_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AER_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AER_APPLICATION_REVIEW_VIEW_TASK,
    PERM_AER_APPLICATION_REVIEW_EXECUTE_TASK,

    PERM_AER_APPLICATION_VERIFICATION_VIEW_TASK,
    PERM_AER_APPLICATION_VERIFICATION_EXECUTE_TASK,

    // VIR
    PERM_VIR_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_VIR_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_VIR_APPLICATION_REVIEW_VIEW_TASK,
    PERM_VIR_APPLICATION_REVIEW_EXECUTE_TASK,

    // AIR
    PERM_AIR_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AIR_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AIR_APPLICATION_REVIEW_VIEW_TASK,
    PERM_AIR_APPLICATION_REVIEW_EXECUTE_TASK,

    // Permit Transfer
    PERM_PERMIT_TRANSFER_A_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_TRANSFER_A_APPLICATION_SUBMIT_EXECUTE_TASK,
    PERM_PERMIT_TRANSFER_B_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_PERMIT_TRANSFER_B_APPLICATION_SUBMIT_EXECUTE_TASK,
    
    PERM_PERMIT_TRANSFER_B_REVIEW_VIEW_TASK,
    PERM_PERMIT_TRANSFER_B_REVIEW_EXECUTE_TASK,
    PERM_PERMIT_TRANSFER_B_PEER_REVIEW_VIEW_TASK,
    PERM_PERMIT_TRANSFER_B_PEER_REVIEW_EXECUTE_TASK,
    
    // Permit Batch Reissue
    PERM_PERMIT_BATCH_REISSUE_SUBMIT_VIEW_TASK,
    PERM_PERMIT_BATCH_REISSUE_SUBMIT_EXECUTE_TASK,
    
    // DRE
    PERM_DRE_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_DRE_APPLICATION_SUBMIT_EXECUTE_TASK,
    
    PERM_DRE_PEER_REVIEW_VIEW_TASK,
    PERM_DRE_PEER_REVIEW_EXECUTE_TASK,
    
    PERM_DRE_MAKE_PAYMENT_VIEW_TASK,
    PERM_DRE_MAKE_PAYMENT_EXECUTE_TASK,
    
    // Non Compliance
    PERM_NON_COMPLIANCE_SUBMIT_VIEW_TASK,
    PERM_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK,
    
    PERM_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK,
    PERM_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK,
    
    // New Entrant Reserve
    PERM_NER_SUBMIT_VIEW_TASK,
    PERM_NER_SUBMIT_EXECUTE_TASK,

    PERM_NER_REVIEW_VIEW_TASK,
    PERM_NER_REVIEW_EXECUTE_TASK,

    PERM_NER_PEER_REVIEW_VIEW_TASK,
    PERM_NER_PEER_REVIEW_EXECUTE_TASK,

    PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_VIEW_TASK,
    PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK,
    PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK,
    PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK,

    PERM_RETURN_OF_ALLOWANCES_SUBMIT_VIEW_TASK,
    PERM_RETURN_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK,
    PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK,
    PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK,

    // DOAL
    PERM_DOAL_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_DOAL_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_DOAL_PEER_REVIEW_VIEW_TASK,
    PERM_DOAL_PEER_REVIEW_EXECUTE_TASK,

    //EMP Issuance Application
    PERM_EMP_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_EMP_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_EMP_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
    PERM_EMP_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK,

    PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
    PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK,
    
    //EMP Variation Application
    PERM_EMP_VARIATION_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_EMP_VARIATION_APPLICATION_SUBMIT_EXECUTE_TASK,
    
    PERM_EMP_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
    PERM_EMP_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK,
    
    PERM_EMP_VARIATION_PEER_REVIEW_VIEW_TASK,
    PERM_EMP_VARIATION_PEER_REVIEW_EXECUTE_TASK,
    
    //EMP Batch Reissue
    PERM_EMP_BATCH_REISSUE_SUBMIT_VIEW_TASK,
    PERM_EMP_BATCH_REISSUE_SUBMIT_EXECUTE_TASK,

    //Aviation AER
    PERM_AVIATION_AER_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AVIATION_AER_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AVIATION_AER_APPLICATION_VERIFICATION_VIEW_TASK,
    PERM_AVIATION_AER_APPLICATION_VERIFICATION_EXECUTE_TASK,

    PERM_AVIATION_AER_APPLICATION_REVIEW_VIEW_TASK,
    PERM_AVIATION_AER_APPLICATION_REVIEW_EXECUTE_TASK,

    // Aviation DRE
    PERM_AVIATION_DRE_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AVIATION_DRE_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AVIATION_DRE_MAKE_PAYMENT_VIEW_TASK,
    PERM_AVIATION_DRE_MAKE_PAYMENT_EXECUTE_TASK,

    PERM_AVIATION_DRE_PEER_REVIEW_VIEW_TASK,
    PERM_AVIATION_DRE_PEER_REVIEW_EXECUTE_TASK,
    
    // Aviation VIR
    PERM_AVIATION_VIR_APPLICATION_SUBMIT_VIEW_TASK,
    PERM_AVIATION_VIR_APPLICATION_SUBMIT_EXECUTE_TASK,

    PERM_AVIATION_VIR_APPLICATION_REVIEW_VIEW_TASK,
    PERM_AVIATION_VIR_APPLICATION_REVIEW_EXECUTE_TASK,

    // Aviation Non Compliance
    PERM_AVIATION_NON_COMPLIANCE_SUBMIT_VIEW_TASK,
    PERM_AVIATION_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK,

    PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK,
    PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK,

    // Aviation Account Closure
    PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_VIEW_TASK,
    PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_EXECUTE_TASK,
    
    // Permissions for Task Assignment
    PERM_TASK_ASSIGNMENT,
    
    // User management
    PERM_ACCOUNT_USERS_EDIT,
    PERM_CA_USERS_EDIT,
    PERM_VB_USERS_EDIT,
    PERM_VB_MANAGE,

    PERM_VB_ACCESS_ALL_ACCOUNTS
}
