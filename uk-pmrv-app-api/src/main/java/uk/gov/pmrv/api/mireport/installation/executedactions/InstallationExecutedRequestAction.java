package uk.gov.pmrv.api.mireport.installation.executedactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestAction;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"accountId", "accountType", "accountName", "accountStatus", "legalEntityName", "permitId", "requestId", "requestType", "requestStatus", "requestActionType", "requestActionSubmitter", "requestActionCompletionDate"})
public class InstallationExecutedRequestAction extends ExecutedRequestAction {
    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Permit ID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonProperty(value = "Emitter type")
    private EmitterType emitterType;

    public InstallationExecutedRequestAction(String accountId,
                                             AccountType accountType,
                                             String accountName,
                                             String accountStatus,
                                             String legalEntityName,
                                             String permitId,
                                             EmitterType emitterType,
                                             String requestId,
                                             String requestType,
                                             String requestStatus,
                                             String requestActionType,
                                             String requestActionSubmitter,
                                             LocalDateTime requestActionCompletionDate) {
        super(accountId, accountName, accountStatus, requestId, requestType, requestStatus, requestActionType, requestActionSubmitter, requestActionCompletionDate);
        this.accountType = accountType;
        this.legalEntityName = legalEntityName;
        this.permitId = permitId;
        this.emitterType = emitterType;
    }

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status", "Legal Entity name", "Permit ID", "Emitter type", "Workflow ID", "Workflow type", "Workflow status", "Timeline event type", "Timeline event Completed by", "Timeline event Date Completed");
    }
}
