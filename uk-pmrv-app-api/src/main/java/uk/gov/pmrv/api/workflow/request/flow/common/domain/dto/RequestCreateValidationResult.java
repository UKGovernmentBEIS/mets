package uk.gov.pmrv.api.workflow.request.flow.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateValidationResult {

    private boolean valid;

    // used for flows that can be allowed conditionally, in contrast to RequestCreateActionType.includedToAvailableWorkflows 
    @JsonIgnore
    @Builder.Default
    private boolean isAvailable = true;
    
    @JsonProperty("accountStatus")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountStatus reportedAccountStatus;

    @JsonProperty("applicableAccountStatuses")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Set<AccountStatus> applicableAccountStatuses = new HashSet<>();

    @JsonProperty("requests")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Set<RequestType> reportedRequestTypes = new HashSet<>();

}
