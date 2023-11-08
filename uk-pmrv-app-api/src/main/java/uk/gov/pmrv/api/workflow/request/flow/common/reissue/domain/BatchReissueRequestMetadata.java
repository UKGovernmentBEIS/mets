package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BatchReissueRequestMetadata extends RequestMetadata {

	@NotBlank
	private String submitterId; //user id
	
	@NotBlank
	private String submitter; //full name
	
	private LocalDate submissionDate;
	
	public abstract Map<Long, ? extends ReissueReport> getAccountsReports();
	
}
