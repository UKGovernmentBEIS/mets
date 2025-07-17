package uk.gov.pmrv.api.account.installation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.service.AccountContactUpdateService;
import uk.gov.pmrv.api.account.service.LegalEntityService;

import java.time.LocalDateTime;

@Validated
@Service
@RequiredArgsConstructor
public class InstallationAccountActivationService {

    private final LegalEntityService legalEntityService;
    private final OperatorAuthorityService operatorauthorityService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final AccountContactUpdateService accountContactUpdateService;
    private final InstallationAccountStatusService installationAccountStatusService;
    private final InstallationAccountTransferCodeGenerator transferCodeGenerator;

    @Transactional
    public void activateAccount(Long accountId, @Valid InstallationAccountDTO accountDTO, String user) {
        installationAccountQueryService.validateAccountNameExistence(accountDTO.getName());
        InstallationAccount account = installationAccountQueryService.getAccountFullInfoById(accountId);

        // Activate LE
        LegalEntity legalEntity = legalEntityService.activateLegalEntity(accountDTO.getLegalEntity());

        // Update account
        account.setLegalEntity(legalEntity);
        account.setAcceptedDate(LocalDateTime.now());

        // Activate account
        final boolean isTransfer = ApplicationType.TRANSFER.equals(accountDTO.getApplicationType());
        if (isTransfer) {
            installationAccountStatusService.handleInstallationAccountForTransferAccepted(accountId);
            account.setTransferCode(transferCodeGenerator.generate());
            account.setTransferCodeStatus(TransferCodeStatus.ACTIVE);
        } else {
            installationAccountStatusService.handleInstallationAccountAccepted(accountId);
        }
        

        // Create operator admin authorities for the created account
        operatorauthorityService.createOperatorAdminAuthority(account.getId(), user);
        
        accountContactUpdateService.assignUserAsDefaultAccountContactPoint(user, account);
    }
}
