package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

@RequiredArgsConstructor
@Component(value = "regulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final AuthService authService;
    private final UserLoginDomainService userLoginDomainService;

    @Order(3)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorAuthorityDeletedEvent(final RegulatorAuthorityDeletionEvent deletionEvent) {
        String userDeleted = deletionEvent.getUserId();
        authService.disableUser(userDeleted);
        userLoginDomainService.deleteByUserId(userDeleted);
    }
}
