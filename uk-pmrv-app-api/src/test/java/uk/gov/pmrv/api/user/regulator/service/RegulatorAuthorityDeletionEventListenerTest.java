package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private RegulatorAuthorityDeletionEventListener regulatorAuthorityDeletionEventListener;

    @Mock
    private AuthService authService;

    @Mock
    private UserLoginDomainService userLoginDomainService;

    @Test
    void onRegulatorAuthorityDeletedEvent_thenDisable() {

        final String userId = "userId";
        final RegulatorAuthorityDeletionEvent deletionEvent = RegulatorAuthorityDeletionEvent.builder()
            .userId(userId)
            .build();

        regulatorAuthorityDeletionEventListener.onRegulatorAuthorityDeletedEvent(deletionEvent);

        verify(authService, times(1)).disableUser(userId);
        verify(userLoginDomainService, times(1)).deleteByUserId(userId);
    }
}
