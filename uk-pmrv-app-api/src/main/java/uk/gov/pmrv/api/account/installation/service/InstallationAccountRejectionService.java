package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.service.LegalEntityService;

@Service
@RequiredArgsConstructor
public class InstallationAccountRejectionService {

    private final InstallationAccountStatusService installationAccountStatusService;
    private final LegalEntityService legalEntityService;
    private final InstallationAccountQueryService installationAccountQueryService;

    public void rejectAccount(Long accountId) {
        InstallationAccount account = installationAccountQueryService.getAccountWithLeById(accountId);
        LegalEntity legalEntity = legalEntityService.getLegalEntityById(account.getLegalEntity().getId());

        // Change Account status to DENIED
        installationAccountStatusService.handleInstallationAccountRejected(accountId);

        // Change LE status to DENIED
        legalEntityService.handleLegalEntityDenied(legalEntity);
    }
}
