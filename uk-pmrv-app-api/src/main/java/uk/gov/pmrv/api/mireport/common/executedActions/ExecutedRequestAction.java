package uk.gov.pmrv.api.mireport.common.executedActions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutedRequestAction {

    @JsonProperty(value = "Account ID")
    private String emitterId;

    @JsonProperty(value = "Account type")
    private AccountType accountType;

    @JsonProperty(value = "Account name")
    private String accountName;

    @JsonProperty(value = "Account status")
    private String accountStatus;

    @JsonProperty(value = "Legal Entity name")
    private String legalEntityName;

    @JsonProperty(value = "Permit ID")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    @JsonProperty(value = "Workflow ID")
    private String requestId;

    @JsonProperty(value = "Workflow type")
    private RequestType requestType;

    @JsonProperty(value = "Workflow status")
    private RequestStatus requestStatus;

    @JsonProperty(value = "Timeline event type")
    private RequestActionType requestActionType;

    @JsonProperty(value = "Timeline event Completed by")
    private String requestActionSubmitter;

    @JsonProperty(value = "Timeline event Date Completed")
    private LocalDateTime requestActionCompletionDate;

    public static final List<String> getColumnNames() {
        return List.of("Account ID", "Account type", "Account name", "Account status",
                "Legal Entity name", "Permit ID", "Workflow ID", "Workflow type", "Workflow status", "Timeline event type",
                "Timeline event Completed by", "Timeline event Date Completed");
    }
}
