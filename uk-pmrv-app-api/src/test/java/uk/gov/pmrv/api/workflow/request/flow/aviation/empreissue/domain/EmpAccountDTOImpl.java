package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpAccountDTOImpl implements EmpAccountDTO {

	private String empId;
	private Long accountId;
	
}
