package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

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
public class EmpBatchReissueRequestMetadata extends BatchReissueRequestMetadata {
	
	@Builder.Default
	private Map<Long, EmpReissueAccountReport> accountsReports = new HashMap<>();

}
