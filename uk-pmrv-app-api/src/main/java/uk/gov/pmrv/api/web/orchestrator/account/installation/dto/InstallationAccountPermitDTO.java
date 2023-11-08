package uk.gov.pmrv.api.web.orchestrator.account.installation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallationAccountPermitDTO {

	private InstallationAccountDTO account;
	private PermitDetailsDTO permit;
}
