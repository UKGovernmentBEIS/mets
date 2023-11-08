package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Request Types.
 */
@Getter
public enum RequestType {

	// INSTALLATION types
    INSTALLATION_ACCOUNT_OPENING("PROCESS_INSTALLATION_ACCOUNT_OPENING", "Account creation", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    SYSTEM_MESSAGE_NOTIFICATION("SYSTEM_MESSAGE_NOTIFICATION", "System Message Notification", null, null, false, true),
    PERMIT_ISSUANCE("PROCESS_PERMIT_ISSUANCE", "Permit Application", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_SURRENDER("PROCESS_PERMIT_SURRENDER", "Permit Surrender", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_REVOCATION("PROCESS_PERMIT_REVOCATION", "Permit Revocation", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_TRANSFER_A("PROCESS_PERMIT_TRANSFER_A", "Permit Transfer Transferring", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_TRANSFER_B("PROCESS_PERMIT_TRANSFER_B", "Permit Transfer Receiving", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_VARIATION("PROCESS_PERMIT_VARIATION", "Permit Variation", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_NOTIFICATION("PROCESS_PERMIT_NOTIFICATION", "Permit Notification", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    PERMIT_BATCH_REISSUE("PROCESS_PERMIT_BATCH_REISSUE", "Permit Batch Reissue", RequestHistoryCategory.CA, AccountType.INSTALLATION, false, true),
    PERMIT_REISSUE("PROCESS_PERMIT_REISSUE", "Permit Reissue", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, false),
    NON_COMPLIANCE("PROCESS_NON_COMPLIANCE", "Non-compliance", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    NER("PROCESS_NER", "NER", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    DOAL("PROCESS_DOAL", "Determination of Activity Level", RequestHistoryCategory.REPORTING, AccountType.INSTALLATION, false, true),
    AER("PROCESS_AER", "Annual emission report", RequestHistoryCategory.REPORTING, AccountType.INSTALLATION, true, true),
    VIR("PROCESS_VIR", "Verifier Improvements Report", RequestHistoryCategory.REPORTING, AccountType.INSTALLATION, false, true),
    AIR("PROCESS_AIR", "Annual Improvements Report", RequestHistoryCategory.REPORTING, AccountType.INSTALLATION, false, true),
    DRE("PROCESS_DRE", "DRE", RequestHistoryCategory.REPORTING, AccountType.INSTALLATION, false, true),
    WITHHOLDING_OF_ALLOWANCES("PROCESS_WITHHOLDING_OF_ALLOWANCES", "Withholding of allowances", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),
    RETURN_OF_ALLOWANCES("PROCESS_RETURN_OF_ALLOWANCES", "Return of allowances", RequestHistoryCategory.PERMIT, AccountType.INSTALLATION, false, true),

    // AVIATION UKETS + CORSIA types
    AVIATION_ACCOUNT_CLOSURE("PROCESS_AVIATION_ACCOUNT_CLOSURE", "Aviation Account Closure", RequestHistoryCategory.PERMIT, AccountType.AVIATION, false, true),
    AVIATION_NON_COMPLIANCE("PROCESS_AVIATION_NON_COMPLIANCE", "Aviation non-compliance", RequestHistoryCategory.PERMIT, AccountType.AVIATION, false, true),
    EMP_BATCH_REISSUE("PROCESS_EMP_BATCH_REISSUE", "EMP Batch Reissue", RequestHistoryCategory.CA, AccountType.AVIATION, false, true),
    EMP_REISSUE("PROCESS_EMP_REISSUE", "EMP Reissue", RequestHistoryCategory.PERMIT, AccountType.AVIATION, false, false),
    AVIATION_VIR("PROCESS_AVIATION_VIR", "Aviation VIR", RequestHistoryCategory.REPORTING, AccountType.AVIATION, false, true),
    
    // AVIATION UKETS types
    EMP_ISSUANCE_UKETS("PROCESS_EMP_ISSUANCE_UKETS", "EMP issuance", RequestHistoryCategory.PERMIT, AccountType.AVIATION, false, true),
    EMP_VARIATION_UKETS("PROCESS_EMP_VARIATION_UKETS", "EMP variation", RequestHistoryCategory.PERMIT, AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION, false, true),
    AVIATION_AER_UKETS("PROCESS_AVIATION_AER_UKETS", "AER (UKETS)", RequestHistoryCategory.REPORTING, AccountType.AVIATION, true, true),
    AVIATION_DRE_UKETS("PROCESS_AVIATION_DRE_UKETS", "DRE (UKETS)", RequestHistoryCategory.REPORTING, AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION,false, true),
    
    // AVIATION CORSIA types
    EMP_ISSUANCE_CORSIA("PROCESS_EMP_ISSUANCE_CORSIA", "CORSIA EMP issuance", RequestHistoryCategory.PERMIT, AccountType.AVIATION, false, true),
    EMP_VARIATION_CORSIA("PROCESS_EMP_VARIATION_CORSIA", "CORSIA EMP variation", RequestHistoryCategory.PERMIT, AccountType.AVIATION, EmissionTradingScheme.CORSIA,false, true),
    AVIATION_AER_CORSIA("PROCESS_AVIATION_AER_CORSIA", "AER (CORSIA)", RequestHistoryCategory.REPORTING, AccountType.AVIATION, true, true),
    
    ;

    /**
     * The id of the bpmn process that will be instantiated for this request type.
     */
    private final String processDefinitionId;

    /**
     * The description of the request type.
     */
    private final String description;

    private final RequestHistoryCategory category;

    private final AccountType accountType;

    private EmissionTradingScheme emissionTradingScheme;

    private final boolean holdHistory;
    
    /**
     * Whether request is displayed when in progress status
     */
    private final boolean displayedInProgress;

    RequestType(String processDefinitionId, String description, RequestHistoryCategory category, AccountType accountType, boolean holdHistory, boolean displayedInProgress) {
        this.processDefinitionId = processDefinitionId;
        this.description = description;
        this.category = category;
        this.accountType = accountType;
        this.holdHistory = holdHistory;
        this.displayedInProgress = displayedInProgress;
    }

    RequestType(String processDefinitionId, String description, RequestHistoryCategory category, AccountType accountType, EmissionTradingScheme emissionTradingScheme, boolean holdHistory, boolean displayedInProgress) {
        this.processDefinitionId = processDefinitionId;
        this.description = description;
        this.category = category;
        this.accountType = accountType;
        this.holdHistory = holdHistory;
        this.displayedInProgress = displayedInProgress;
        this.emissionTradingScheme = emissionTradingScheme;
    }

    public static Set<RequestType> getCascadableRequestTypes() {
        return Set.of(PERMIT_TRANSFER_A);
    }
    
    public static Set<RequestType> getNotDisplayedInProgressRequestTypes() {
    	return Stream.of(RequestType.values())
                .filter(type -> !type.isDisplayedInProgress())
                .collect(Collectors.toSet());
    }
    
    public static Set<RequestType> getRequestTypesByCategory(RequestHistoryCategory category) {
        return Stream.of(RequestType.values())
            .filter(type -> type.getCategory() == category)
            .collect(Collectors.toSet());
    }

    public static Set<RequestType> getAvailableForAccountCreateRequestTypes(@NotNull AccountType accountType, @NotNull EmissionTradingScheme emissionTradingScheme) {
        Set<RequestType> requestTypes = Set.of(
                PERMIT_SURRENDER,
                PERMIT_REVOCATION,
                PERMIT_TRANSFER_A,
                PERMIT_VARIATION,
                PERMIT_NOTIFICATION,
                NON_COMPLIANCE,
                NER,
                WITHHOLDING_OF_ALLOWANCES,
                RETURN_OF_ALLOWANCES,
                AIR,
                DOAL,

                EMP_VARIATION_UKETS,
                AVIATION_ACCOUNT_CLOSURE,
                AVIATION_NON_COMPLIANCE,
                
                EMP_VARIATION_CORSIA
        );
        return requestTypes.stream()
                .filter(requestType -> accountType.equals(requestType.getAccountType()))
                .filter(requestType -> requestType.getEmissionTradingScheme() == null || requestType.getEmissionTradingScheme().equals(emissionTradingScheme))
                .collect(Collectors.toSet());
    }

    public static Set<RequestType> getAvailableForReportingCreateRequestTypes(@NotNull AccountType accountType, @NotNull EmissionTradingScheme emissionTradingScheme) {
        Set<RequestType> requestTypes = Set.of(
                AER,
                DRE,

                AVIATION_DRE_UKETS
        );

        return requestTypes.stream()
                .filter(requestType -> accountType.equals(requestType.getAccountType()))
                .filter(requestType -> requestType.getEmissionTradingScheme() == null || requestType.getEmissionTradingScheme().equals(emissionTradingScheme))
                .collect(Collectors.toSet());
    }
}
