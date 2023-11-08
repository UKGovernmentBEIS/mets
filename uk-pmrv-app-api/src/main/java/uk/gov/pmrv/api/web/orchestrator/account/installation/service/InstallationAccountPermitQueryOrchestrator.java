package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.transform.InstallationAccountHeaderInfoMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstallationAccountPermitQueryOrchestrator {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final ApprovedInstallationAccountQueryService approvedInstallationAccountQueryService;
    private final PermitQueryService permitQueryService;
    private static final InstallationAccountHeaderInfoMapper ACCOUNT_HEADER_INFO_MAPPER = Mappers.getMapper(InstallationAccountHeaderInfoMapper.class);

    public InstallationAccountPermitDTO getAccountWithPermit(Long accountId) {
    	InstallationAccountDTO account = installationAccountQueryService.getAccountDTOById(accountId);
		Optional<PermitDetailsDTO> permitDetailsOpt = permitQueryService.getPermitDetailsByAccountId(accountId);

		return InstallationAccountPermitDTO.builder()
        		.account(account)
        		.permit(permitDetailsOpt.orElse(null))
        		.build();
    }

    public Optional<InstallationAccountHeaderInfoDTO> getAccountHeaderInfoWithPermitId(Long accountId) {
        Optional<InstallationAccountHeaderInfoDTO> accountHeaderInfo =
            approvedInstallationAccountQueryService.getApprovedAccountById(accountId).map(ACCOUNT_HEADER_INFO_MAPPER::toAccountHeaderInfoDTO);
        accountHeaderInfo.ifPresent(headerInfo -> headerInfo.setPermitId(permitQueryService.getPermitIdByAccountId(accountId).orElse(null)));
        return accountHeaderInfo;
    }
}
