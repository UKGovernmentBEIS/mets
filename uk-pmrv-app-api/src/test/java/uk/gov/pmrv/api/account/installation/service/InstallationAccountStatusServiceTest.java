package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountStatusServiceTest {

    @InjectMocks
    private InstallationAccountStatusService service;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    void handlePermitSurrenderGranted() {
        Long accountId = 1L;

        InstallationAccount account = InstallationAccount.builder().id(accountId).status(InstallationAccountStatus.LIVE).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.handlePermitSurrenderGranted(accountId);

        assertThat(account.getStatus()).isEqualTo(InstallationAccountStatus.AWAITING_SURRENDER);
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }
}
