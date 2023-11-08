package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private OperatorAuthorityDeletionEventListener listener;

    @Mock
    private UserLoginDomainService userLoginDomainService;

    @Test
    void onOperatorAuthorityDeletionEvent_when_no_other_authorities() {
        String userId = "userId";
        OperatorAuthorityDeletionEvent event = OperatorAuthorityDeletionEvent.builder()
            .userId(userId)
            .accountId(1L)
            .existAuthoritiesOnOtherAccounts(false)
            .build();

        listener.onOperatorAuthorityDeletionEvent(event);

        verify(userLoginDomainService, times(1)).deleteByUserId(userId);
    }

    @Test
    void onOperatorAuthorityDeletionEvent_when_other_authorities_exist() {
        String userId = "userId";
        OperatorAuthorityDeletionEvent event = OperatorAuthorityDeletionEvent.builder()
            .userId(userId)
            .accountId(1L)
            .existAuthoritiesOnOtherAccounts(true)
            .build();

        listener.onOperatorAuthorityDeletionEvent(event);

        verifyNoInteractions(userLoginDomainService);
    }
}