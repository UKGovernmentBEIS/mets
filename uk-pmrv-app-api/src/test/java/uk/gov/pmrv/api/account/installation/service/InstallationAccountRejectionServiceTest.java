package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.service.LegalEntityService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountRejectionServiceTest {

    @InjectMocks
    private InstallationAccountRejectionService service;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void rejectAccount() {
        final Long accountId = 1L;
        final Long legalEntityId = 2L;
        LegalEntity legalEntity = LegalEntity.builder().id(legalEntityId).build();
        InstallationAccount account = InstallationAccount.builder().id(accountId).legalEntity(legalEntity).build();

        when(installationAccountQueryService.getAccountWithLeById(accountId)).thenReturn(account);
        when(legalEntityService.getLegalEntityById(legalEntityId)).thenReturn(legalEntity);

        // Invoke
        service.rejectAccount(accountId);

        // Verify
        verify(installationAccountQueryService, times(1)).getAccountWithLeById(accountId);
        verify(legalEntityService, times(1)).getLegalEntityById(legalEntityId);
        verify(installationAccountStatusService, times(1)).handleInstallationAccountRejected(accountId);
        verify(legalEntityService, times(1)).handleLegalEntityDenied(legalEntity);
    }
}
