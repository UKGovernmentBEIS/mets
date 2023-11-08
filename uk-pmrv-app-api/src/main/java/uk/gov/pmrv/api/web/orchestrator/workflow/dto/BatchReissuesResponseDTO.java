package uk.gov.pmrv.api.web.orchestrator.workflow.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;

@Data
@Builder
public class BatchReissuesResponseDTO {

	@JsonUnwrapped
	private RequestDetailsSearchResults requestDetailsSearchResults;
	
	private boolean canInitiateBatchReissue;
	
}
