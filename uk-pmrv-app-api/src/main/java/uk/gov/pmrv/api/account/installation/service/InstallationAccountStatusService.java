package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccountStatusChangedEvent;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;

@Service
@RequiredArgsConstructor
public class InstallationAccountStatusService {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitGranted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.LIVE);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitRejected(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.PERMIT_REFUSED);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitDeemedWithdrawn(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.DEEMED_WITHDRAWN);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'LIVE'}")
    public void handlePermitSurrenderGranted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.AWAITING_SURRENDER);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'AWAITING_SURRENDER'}")
    public void handleSurrenderCessationCompleted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.SURRENDERED);
    }
    
    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'LIVE'}")
    public void handlePermitRevoked(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.AWAITING_REVOCATION);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'AWAITING_REVOCATION'}")
    public void handleRevocationCessationCompleted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.REVOKED);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'UNAPPROVED'}")
    public void handleInstallationAccountAccepted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.NEW);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'UNAPPROVED'}")
    public void handleInstallationAccountForTransferAccepted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.AWAITING_TRANSFER);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'UNAPPROVED'}")
    public void handleInstallationAccountRejected(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.DENIED);
    }
    
    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'AWAITING_TRANSFER'}")
    public void handleTransferBGranted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.LIVE);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'LIVE'}")
    public void handleTransferAGranted(final Long accountId) {
        updateAccountStatus(accountId, InstallationAccountStatus.TRANSFERRED);
    }

    private void updateAccountStatus(final Long accountId, final InstallationAccountStatus newStatus) {
        InstallationAccount account = installationAccountQueryService.getAccountById(accountId);

        account.setStatus(newStatus);

        publisher.publishEvent(InstallationAccountStatusChangedEvent.builder()
            .accountId(accountId)
            .status(newStatus)
            .build());
    }
}
