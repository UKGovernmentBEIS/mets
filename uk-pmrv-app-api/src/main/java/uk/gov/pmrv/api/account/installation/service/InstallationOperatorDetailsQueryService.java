package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountToInstallationOperatorDetailsMapper;

@Service
@RequiredArgsConstructor
public class InstallationOperatorDetailsQueryService {

	private final InstallationAccountQueryService installationAccountQueryService;
	private static final InstallationAccountToInstallationOperatorDetailsMapper INSTALLATION_ACCOUNT_TO_INSTALLATION_OPERATOR_DETAILS_MAPPER = Mappers
			.getMapper(InstallationAccountToInstallationOperatorDetailsMapper.class);
    
    public InstallationOperatorDetails getInstallationOperatorDetails(Long accountId) {
		return INSTALLATION_ACCOUNT_TO_INSTALLATION_OPERATOR_DETAILS_MAPPER
				.toPermitInstallationOperatorDetails(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(accountId));
    }

	public InstallationCategory getInstallationCategory(Long accountId) {
		return installationAccountQueryService.getAccountInstallationCategoryById(accountId);
	}
}
