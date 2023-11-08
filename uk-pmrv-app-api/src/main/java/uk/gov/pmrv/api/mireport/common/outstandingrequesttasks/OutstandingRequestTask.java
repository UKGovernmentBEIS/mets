package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutstandingRequestTask {

    @JsonProperty(value = "Account ID")
    private String emitterId;

    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Account name")
    private String accountName;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Workflow ID")
    private String requestId;

    @JsonProperty(value = "Workflow type")
    private RequestType requestType;

    @JsonProperty(value = "Workflow task name")
    private RequestTaskType requestTaskType;

    private String requestTaskAssignee;

    @JsonProperty(value = "Workflow task assignee")
    private String requestTaskAssigneeName;

    @JsonProperty(value = "Workflow task due date")
    private LocalDate requestTaskDueDate;

    @JsonIgnore
    private LocalDate requestTaskPausedDate;

    @JsonProperty(value = "Workflow task days remaining")
    private Long requestTaskRemainingDays;
    
    public OutstandingRequestTask(final String emitterId,
                                  final AccountType accountType, 
                                  final String accountName, 
                                  final String legalEntityName,
                                  final String requestId, 
                                  final RequestType requestType, 
                                  final RequestTaskType requestTaskType,
                                  final String requestTaskAssignee,
                                  final LocalDate requestTaskDueDate, 
                                  final LocalDate requestTaskPausedDate) {
        this.emitterId = emitterId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.legalEntityName = legalEntityName;
        this.requestId = requestId;
        this.requestType = requestType;
        this.requestTaskType = requestTaskType;
        this.requestTaskAssignee = requestTaskAssignee;
        this.requestTaskDueDate = requestTaskDueDate;
        if (requestTaskDueDate != null) {
            this.requestTaskRemainingDays = ChronoUnit.DAYS.between(requestTaskPausedDate == null ? LocalDate.now() : requestTaskPausedDate, requestTaskDueDate);
        }
    }

    public static final List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name",
                "Legal Entity name", "Workflow ID", "Workflow type", "Workflow task name",
                "Workflow task assignee", "Workflow task due date", "Workflow task days remaining");
    }
}
