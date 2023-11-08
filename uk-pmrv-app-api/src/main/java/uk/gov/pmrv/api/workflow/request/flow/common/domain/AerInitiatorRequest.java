package uk.gov.pmrv.api.workflow.request.flow.common.domain;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AerInitiatorRequest {

	@NotNull
	private RequestType type;
	
	private LocalDateTime submissionDateTime;
	
}
