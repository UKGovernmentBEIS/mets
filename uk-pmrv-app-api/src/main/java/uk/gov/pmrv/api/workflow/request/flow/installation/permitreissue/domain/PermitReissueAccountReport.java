package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueReport;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitReissueAccountReport extends ReissueReport {

	private String permitId;
	private String installationName; //accountName
	private String operatorName;
	
}
