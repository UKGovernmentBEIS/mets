package uk.gov.pmrv.api.mireport.aviation.executedactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestAction;
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
@JsonPropertyOrder({"accountId", "accountType", "accountName", "accountStatus", "legalEntityName", "permitId", "requestId", "requestType", "requestStatus", "requestActionType", "requestActionSubmitter", "requestActionCompletionDate", "crcoCode"})
public class AviationExecutedRequestAction extends ExecutedRequestAction {
    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Permit ID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonProperty(value = "CRCO code")
    private String crcoCode;

    public AviationExecutedRequestAction(String accountId,
                                         AccountType accountType,
                                         String accountName,
                                         String accountStatus,
                                         String legalEntityName,
                                         String permitId,
                                         String requestId,
                                         String requestType,
                                         String requestStatus,
                                         String requestActionType,
                                         String requestActionSubmitter,
                                         LocalDateTime requestActionCompletionDate,
                                         String crcoCode) {
        super(accountId, accountName, accountStatus, requestId, requestType, requestStatus, requestActionType, requestActionSubmitter, requestActionCompletionDate);
        this.accountType = accountType;
        this.legalEntityName = legalEntityName;
        this.permitId = permitId;
        this.crcoCode = crcoCode;
    }

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status", "Legal Entity name", "Permit ID", "Workflow ID", "Workflow type", "Workflow status", "Timeline event type", "Timeline event Completed by", "Timeline event Date Completed", "CRCO code");
    }
}
