package uk.gov.pmrv.api.permit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.service.ApprovedInstallationAccountQueryService;
import uk.gov.pmrv.api.common.constants.StateConstants;
import uk.gov.pmrv.api.common.exception.BusinessException;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

/**
 * Generates permit identifier according to specs:
 * <br/>
 * https://pmo.trasys.be/confluence/display/PMRV/Permit+Id
 */
@Service
@RequiredArgsConstructor
public class PermitIdentifierGenerator {

    private final ApprovedInstallationAccountQueryService approvedInstallationAccountService;

    String generate(Long accountId) {
        InstallationAccountInfoDTO accountInfo = approvedInstallationAccountService.getApprovedAccountById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        String countryCode = StateConstants.UK;
        String authorityCode = accountInfo.getCompetentAuthority().getOneLetterCode();
        String typeCode = accountInfo.getAccountType().getCode();
        String accountIdFormatted = String.format("%05d", accountInfo.getId());

        return String.format("%s-%s-%s-%s", countryCode, authorityCode, typeCode, accountIdFormatted);
    }
}
