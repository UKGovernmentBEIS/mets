package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AviationAccountIdAndNameDTOImpl implements AviationAccountIdAndNameDTO {
	
	private Long accountId;
	private String accountName;

}