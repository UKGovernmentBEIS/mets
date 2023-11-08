package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpReissueAccountDetails {

	private String empId;
	private String accountName;
	
}
