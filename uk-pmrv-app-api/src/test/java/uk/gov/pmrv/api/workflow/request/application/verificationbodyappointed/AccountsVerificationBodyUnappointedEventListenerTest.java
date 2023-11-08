package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.event.AccountsVerificationBodyUnappointedEvent;

@ExtendWith(MockitoExtension.class)
class AccountsVerificationBodyUnappointedEventListenerTest {

    @InjectMocks
    private AccountsVerificationBodyUnappointedEventListener listener;

    @Mock
    private RequestVerificationBodyService requestVerificationBodyService;

    @Test
    void onAccountsVerificationBodyUnappointedEvent() {
        Set<Long> accountIds = Set.of(1L, 2L);
        AccountsVerificationBodyUnappointedEvent event =
            AccountsVerificationBodyUnappointedEvent.builder().accountIds(accountIds).build();

        listener.onAccountsVerificationBodyUnappointedEvent(event);

        verify(requestVerificationBodyService, times(1)).unappointVerificationBodyFromRequestsOfAccounts(accountIds);
    }
}