package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

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
public class EmpReissueAccountReport extends ReissueReport {

	private String empId;
	private String accountName;
	
}