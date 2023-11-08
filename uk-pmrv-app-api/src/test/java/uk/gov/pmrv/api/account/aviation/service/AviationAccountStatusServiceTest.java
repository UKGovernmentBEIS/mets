package uk.gov.pmrv.api.account.aviation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;


@ExtendWith(MockitoExtension.class)
class AviationAccountStatusServiceTest {

    @InjectMocks
    private AviationAccountStatusService aviationAccountStatusService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Test
    void handleEmpGranted() {
        Long accountId = 1L;

        AviationAccount account = createAviationAccountWithStatusNew(accountId);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        aviationAccountStatusService.handleEmpApproved(accountId);

        assertThat(account.getStatus()).isEqualTo(AviationAccountStatus.LIVE);
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
    }
    
    @Test
    void handleCloseAccount() {
        Long accountId = 1L;

        AviationAccount account = createAviationAccountWithStatusNew(accountId);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        aviationAccountStatusService.handleCloseAccount(accountId);

        assertThat(account.getStatus()).isEqualTo(AviationAccountStatus.CLOSED);
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
    }
    
	private AviationAccount createAviationAccountWithStatusNew(Long accountId) {
		AviationAccount account = AviationAccount
				.builder()
				.id(accountId)
				.status(AviationAccountStatus.NEW)
				.build();
		return account;
	}
}