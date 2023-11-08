package uk.gov.pmrv.api.workflow.request.flow.common.domain.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateRequestTypeValidationResult {
	
	private boolean valid;

    @Builder.Default
    private Set<RequestType> reportedRequestTypes = new HashSet<>();
}
