package uk.gov.pmrv.api.mireport.installation.outstandingrequesttasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTask;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"accountId", "accountType", "accountName", "legalEntityName", "requestId", "requestType", "requestTaskType", "requestTaskAssigneeName", "requestTaskDueDate", "requestTaskRemainingDays"})
public class InstallationOutstandingRequestTask extends OutstandingRequestTask {

    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Emitter type")
    private EmitterType emitterType;

    public InstallationOutstandingRequestTask(String accountId,
                                              AccountType accountType,
                                              String accountName,
                                              String legalEntityName,
                                              EmitterType emitterType,
                                              String requestId,
                                              String requestType,
                                              String requestTaskType,
                                              String requestTaskAssignee,
                                              LocalDate requestTaskDueDate,
                                              LocalDate requestTaskPausedDate) {
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
        this.emitterType = emitterType;
    }

    public static List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account type", "Account name", "Legal Entity name", "Emitter type", "Workflow ID", "Workflow type", "Workflow task name", "Workflow task assignee", "Workflow task due date", "Workflow task days remaining");
    }
}
