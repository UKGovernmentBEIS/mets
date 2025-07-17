package uk.gov.pmrv.api.user.operator.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;

@RequiredArgsConstructor
@Component(value =  "operatorAuthorityDeletionEventListener")
public class OperatorAuthorityDeletionEventListener {

    private final UserLoginDomainService userLoginDomainService;

    @Order(3)
    @EventListener(OperatorAuthorityDeletionEvent.class)
    public void onOperatorAuthorityDeletionEvent(OperatorAuthorityDeletionEvent deletionEvent) {
        if(!deletionEvent.isExistAuthoritiesOnOtherAccounts()) {
            userLoginDomainService.deleteByUserId(deletionEvent.getUserId());
        }
    }
}
