package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermitEntityAccountDTOImpl implements PermitEntityAccountDTO {
	private String permitEntityId;
	private Long accountId;
	private PermitType permitType;

	@Override
	public String getPermitEntityId() {
		return permitEntityId;
	}

	@Override
	public Long getAccountId() {
		return accountId;
	}

	@Override
	public PermitType getPermitType() {
		return permitType;
	}

}