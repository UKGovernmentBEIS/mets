package uk.gov.pmrv.api.user.verifier.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

@RequiredArgsConstructor
@Component(value =  "verifierUserDeletionEventListener")
public class VerifierAuthorityDeletionEventListener {

    private final AuthService authService;
    private final UserLoginDomainService userLoginDomainService;

    @Order(3)
    @EventListener(VerifierAuthorityDeletionEvent.class)
    public void onVerifierUserDeletedEvent(VerifierAuthorityDeletionEvent deletionEvent) {
        String userDeleted = deletionEvent.getUserId();
        authService.disableUser(userDeleted);
        userLoginDomainService.deleteByUserId(userDeleted);
    }
}
