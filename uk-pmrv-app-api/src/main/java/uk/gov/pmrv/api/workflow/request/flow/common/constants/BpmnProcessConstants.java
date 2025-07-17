package uk.gov.pmrv.api.workflow.request.flow.common.constants;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;

/**
 * Encapsulates constants related to BPMN Process
 */
@UtilityClass
public class BpmnProcessConstants {
    
    public static final String _EXPIRATION_DATE = "ExpirationDate";
    public static final String _FIRST_REMINDER_DATE = "FirstReminderDate";
    public static final String _SECOND_REMINDER_DATE = "SecondReminderDate";

    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_STATUS = "requestStatus";
    public static final String REQUEST_TASK_TYPE = "requestTaskType";
    public static final String REQUEST_TASK_ASSIGNEE = "requestTaskAssignee";
    public static final String REQUEST_TYPE = "requestType";
    public static final String REQUEST_TYPE_DYNAMIC_TASK_PREFIX = "requestTypeDynamicTaskPrefix";
    public static final String REQUEST_INITIATOR_ROLE_TYPE = "requestInitiatorRoleType";
    public static final String REQUEST_DELETE_UPON_TERMINATE = "requestDeleteUponTerminate";
    public static final String BUSINESS_KEY = "businessKey";

    public static final String VERIFICATION_BODY_STATE_CHANGED = "verificationBodyStateChanged";

    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNT_IDS = "accountIds";

    // account opening
    public static final String APPLICATION_ACCEPTED = "applicationAccepted";
    public static final String APPLICATION_TYPE_IS_TRANSFER = "applicationTypeIsTransfer";
    
    // application review
    public static final String APPLICATION_REVIEW_EXPIRATION_DATE = RequestExpirationType.APPLICATION_REVIEW.getCode() + _EXPIRATION_DATE;
    public static final String REVIEW_DETERMINATION = "reviewDetermination";
    public static final String REVIEW_OUTCOME = "reviewOutcome";
    public static final String REVIEW_DECISION_TYPE_OUTCOME = "reviewDecisionTypeOutcome";

    // rfi
    public static final String RFI_REQUESTED = "rfiRequested";
    public static final String RFI_START_TIME = "rfiStartTime";
    public static final String RFI_EXPIRATION_DATE = RequestExpirationType.RFI.getCode() + _EXPIRATION_DATE;
    public static final String RFI_FIRST_REMINDER_DATE = RequestExpirationType.RFI.getCode() + _FIRST_REMINDER_DATE;
    public static final String RFI_SECOND_REMINDER_DATE = RequestExpirationType.RFI.getCode() + _SECOND_REMINDER_DATE;
    public static final String RFI_OUTCOME = "rfiOutcome";

    // Request for Determination Extension (RDE)
    public static final String RDE_REQUESTED = "rdeRequested";
    public static final String RDE_EXPIRATION_DATE = RequestExpirationType.RDE.getCode() + _EXPIRATION_DATE;
    public static final String RDE_FIRST_REMINDER_DATE = RequestExpirationType.RDE.getCode() + _FIRST_REMINDER_DATE;
    public static final String RDE_SECOND_REMINDER_DATE = RequestExpirationType.RDE.getCode() + _SECOND_REMINDER_DATE;
    public static final String RDE_OUTCOME = "rdeOutcome";
    
    // surrender
    public static final String SURRENDER_OUTCOME = "surrenderOutcome";
    public static final String SURRENDER_REMINDER_NOTICE_DATE = "surrenderReminderNoticeDate";

    // Permit Notification
    public static final String NOTIFICATION_OUTCOME = "notificationOutcome";
    public static final String FOLLOW_UP_RESPONSE_NEEDED = "followUpResponseNeeded";
    public static final String FOLLOW_UP_RESPONSE_EXPIRATION_DATE = RequestExpirationType.FOLLOW_UP_RESPONSE.getCode() + _EXPIRATION_DATE;
    public static final String FOLLOW_UP_TIMER_EXTENDED = "followUpTimerExtended";
    
    // revocation
    public static final String REVOCATION_OUTCOME = "revocationOutcome";
    public static final String REVOCATION_EFFECTIVE_DATE = "revocationEffectiveDate";
    public static final String REVOCATION_REMINDER_EFFECTIVE_DATE = "revocationReminderEffectiveDate";
    
    // permit variation
    public static final String PERMIT_VARIATION_SUBMIT_OUTCOME = "permitVariationSubmitOutcome";
    
    // permit transfer
    public static final String PERMIT_TRANSFER_TRANSFERRING_BUSINESS_KEY = "permitTransferTransferringBusinessKey";
    public static final String PERMIT_TRANSFER_RECEIVING_BUSINESS_KEY = "permitTransferReceivingBusinessKey";
    public static final String PERMIT_TRANSFER_SUBMIT_OUTCOME = "permitTransferSubmitOutcome";
    
    // batch reissue
    public static final String BATCH_REQUEST_BUSINESS_KEY = "batchRequestBusinessKey";
    public static final String BATCH_NUMBER_OF_ACCOUNTS_COMPLETED = "batchAccountsCompleted";
    public static final String REISSUE_REQUEST_ID = "reissueRequestId";
    public static final String REISSUE_REQUEST_SUCCEEDED = "reissueRequestSucceeded";
    
    // non compliance
    public static final String NON_COMPLIANCE_OUTCOME = "nonComplianceOutcome";
    public static final String CIVIL_PENALTY_LIABLE = "civilPenaltyLiable";
    public static final String DAILY_PENALTY_LIABLE = "dailyPenaltyLiable";
    public static final String NOI_PENALTY_LIABLE = "noiPenaltyLiable";
    
    // NER
    public static final String NER_SUBMIT_OUTCOME = "nerSubmitOutcome";
    public static final String SEND_NOTICE = "sendNotice";
    public static final String AUTHORITY_RESPONSE = "authorityResponse";

    // DOAL
    public static final String DOAL_SUBMIT_OUTCOME = "doalSubmitOutcome";
    public static final String DOAL_DETERMINATION = "doalDetermination";
    public static final String DOAL_SEND_NOTICE = "doalSendNotice";
    public static final String DOAL_AUTHORITY_RESPONSE = "doalAuthorityResponse";

    // AER
    public static final String AER_REQUIRED = "aerRequired";
    public static final String AER_OUTCOME = "aerOutcome";
    public static final String AER_REVIEW_OUTCOME = "aerReviewOutcome";
    public static final String AER_EXPIRATION_DATE = RequestExpirationType.AER.getCode() + _EXPIRATION_DATE;
    public static final String AER_MARK_NOT_REQUIRED = "aerMarkNotRequired";

    // DRE
    public static final String DRE_SUBMIT_OUTCOME = "dreSubmitOutcome";
    public static final String DRE_IS_PAYMENT_REQUIRED = "paymentRequired";

    //AVIATION DOE CORSIA
    public static final String AVIATION_DOE_CORSIA_SUBMIT_OUTCOME = "aviationDoECorsiaSubmitOutcome";
    public static final String AVIATION_DOE_CORSIA_IS_PAYMENT_REQUIRED = "paymentRequired";

    // VIR
    public static final String VIR_NEEDS_IMPROVEMENTS = "virNeedsImprovements";
    public static final String VIR_EXPIRATION_DATE = RequestExpirationType.VIR.getCode() + _EXPIRATION_DATE;
    public static final String VIR_RESPONSE_COMMENT_SUBMITTED = "virResponseCommentSubmitted";
    
    // AIR
    public static final String AIR_EXPIRATION_DATE = RequestExpirationType.AIR.getCode() + _EXPIRATION_DATE;
    public static final String AIR_NEEDS_IMPROVEMENTS = "airNeedsImprovements";
    public static final String AIR_RESPONSE_COMMENT_SUBMITTED = "airResponseCommentSubmitted";

    //payment
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String PAYMENT_OUTCOME = "paymentOutcome";
    public static final String PAYMENT_REVIEW_OUTCOME = "paymentReviewOutcome";
    public static final String PAYMENT_EXPIRES = "paymentExpires";
    public static final String SKIP_PAYMENT = "skipPayment";
    public static final String PAYMENT_EXPIRATION_DATE = RequestExpirationType.PAYMENT.getCode() + _EXPIRATION_DATE;

    //withholding of allowances
    public static final String WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME = "withholdingOfAllowancesSubmitOutcome";
    public static final String WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_OUTCOME = "withholdingOfAllowancesWithdrawalOutcome";

    //return of allowances
    public static final String RETURN_OF_ALLOWANCES_SUBMIT_OUTCOME = "returnOfAllowancesSubmitOutcome";

    //installation inspection
    public static final String INSTALLATION_AUDIT_SUBMIT_OUTCOME = "installationAuditSubmitOutcome";
    public static final String INSTALLATION_ONSITE_INSPECTION_SUBMIT_OUTCOME = "installationOnsiteInspectionSubmitOutcome";
    public static final String INSTALLATION_AUDIT_EXPIRATION_DATE = RequestExpirationType.INSTALLATION_AUDIT.getCode() + _EXPIRATION_DATE;
    public static final String INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE = RequestExpirationType.INSTALLATION_ONSITE_INSPECTION.getCode() + _EXPIRATION_DATE;
    public static final String INSTALLATION_INSPECTION_ARE_FOLLOWUP_ACTIONS_REQUIRED = "installationInspectionAreFollowupActionsRequired";

    //BDR
    public static final String BDR_INITIATION_TYPE = "bdrInitiationType";
    public static final String BDR_EXPIRATION_DATE = RequestExpirationType.BDR.getCode() + _EXPIRATION_DATE;
    public static final String BDR_OUTCOME = "bdrOutcome";
    public static final String BDR_REGULATOR_REVIEW_OUTCOME = "bdrRegulatorReviewOutcome";

    //Permanent Cessation
    public static final String PERMANENT_CESSATION_SUBMIT_OUTCOME = "permanentCessationSubmitOutcome";

    //ALR
    public static final String ALR_EXPIRATION_DATE = RequestExpirationType.ALR.getCode() + _EXPIRATION_DATE;
    public static final String ALR_OUTCOME = "alrOutcome";

    // messaging
    public static final String PROCESS_TO_MESSAGE_BUSINESS_KEY = "processToMessageBusinessKey";
    public static final String VARIABLES = "variables";

    //aviation aer
    public static final String AVIATION_AER_EXPIRATION_DATE = RequestExpirationType.AVIATION_AER.getCode() + _EXPIRATION_DATE;
    public static final String AVIATION_AER_OUTCOME = "aviationAerOutcome";
    public static final String AVIATION_AER_REVIEW_OUTCOME = "aviationAerReviewOutcome";
    public static final String AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME = "aviationAerCorsiaAnnualOffsettingSubmitOutcome";
    public static final String AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME = "aviationAerCorsia3YearPeriodOffsettingSubmitOutcome";

    //aviation vir
    public static final String AVIATION_VIR_EXPIRATION_DATE = RequestExpirationType.AVIATION_VIR.getCode() + _EXPIRATION_DATE;
    
    // emp variation
    public static final String EMP_VARIATION_SUBMIT_OUTCOME = "empVariationSubmitOutcome";
}
