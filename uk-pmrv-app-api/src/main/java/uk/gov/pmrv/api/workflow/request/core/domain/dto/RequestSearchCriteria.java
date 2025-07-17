package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestSearchCriteria {
	
	@NotNull
	private String resourceType;
	
	@NotNull
	private String resourceId;
    
    @Builder.Default
    private Set<RequestType> requestTypes = new HashSet<>();

    @Builder.Default
    private Set<RequestStatus> requestStatuses = new HashSet<>();

    @NotNull
    private RequestHistoryCategory category;
    
    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
    
    
}
