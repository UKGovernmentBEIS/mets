package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTask;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"accountId", "accountType", "accountName", "legalEntityName", "requestId", "requestType", "requestTaskType", "requestTaskAssigneeName", "requestTaskDueDate", "requestTaskRemainingDays", "crcoCode"})
public class AviationOutstandingRequestTask extends OutstandingRequestTask {
    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "CRCO code")
    private String crcoCode;

    public AviationOutstandingRequestTask(String accountId,
                                          AccountType accountType,
                                          String accountName,
                                          String legalEntityName,
                                          String requestId,
                                          String requestType,
                                          String requestTaskType,
                                          String requestTaskAssignee,
                                          LocalDate requestTaskDueDate,
                                          LocalDate requestTaskPausedDate,
                                          String crcoCode) {
        super(accountId,
                accountName,
                requestId,
                requestType,
                requestTaskType,
                requestTaskAssignee,
                requestTaskDueDate,
                requestTaskPausedDate);
        this.accountType = accountType;
        this.legalEntityName = legalEntityName;
        this.crcoCode = crcoCode;
    }

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account type", "Account name", "Legal Entity name", "Workflow ID", "Workflow type", "Workflow task name", "Workflow task assignee", "Workflow task due date", "Workflow task days remaining", "CRCO code");
    }
}
