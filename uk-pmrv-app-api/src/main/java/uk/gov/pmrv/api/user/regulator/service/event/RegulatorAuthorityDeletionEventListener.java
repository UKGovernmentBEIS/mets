package uk.gov.pmrv.api.user.regulator.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;

@RequiredArgsConstructor
@Component(value = "regulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final UserLoginDomainService userLoginDomainService;

    @Order(3)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorAuthorityDeletedEvent(final RegulatorAuthorityDeletionEvent deletionEvent) {
        String userDeleted = deletionEvent.getUserId();
        userLoginDomainService.deleteByUserId(userDeleted);
    }
}
