package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitBatchReissueRequestMetadata extends BatchReissueRequestMetadata {
	
	@Builder.Default
	private Map<Long, PermitReissueAccountReport> accountsReports = new HashMap<>();
	
}
