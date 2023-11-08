package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitVariationRequestInfo {

	private String id; //request id
	private LocalDateTime submissionDate; //request submission date
	private LocalDateTime endDate; //request end date
	private PermitVariationRequestMetadata metadata;
}
