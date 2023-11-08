package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstallationAccountIdAndNameAndLegalEntityNameDTOImpl
		implements InstallationAccountIdAndNameAndLegalEntityNameDTO {
	private Long accountId;
	private String accountName;
	private String legalEntityName;

	@Override
	public Long getAccountId() {
		return accountId;
	}

	@Override
	public String getAccountName() {
		return accountName;
	}

	@Override
	public String getLegalEntityName() {
		return legalEntityName;
	}
}
