package uk.gov.pmrv.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.account.service.AccountContactDeletionService;

@RequiredArgsConstructor
@Component(value =  "accountOperatorAuthorityDeletionEventListener")
public class OperatorAuthorityDeletionEventListener {

    private final AccountContactDeletionService accountContactDeletionService;

    @Order(1)
    @EventListener(OperatorAuthorityDeletionEvent.class)
    public void onOperatorUserDeletionEventListener(OperatorAuthorityDeletionEvent deletionEvent) {
        accountContactDeletionService.removeUserFromAccountContacts(deletionEvent.getUserId(), deletionEvent.getAccountId());
    }
}
