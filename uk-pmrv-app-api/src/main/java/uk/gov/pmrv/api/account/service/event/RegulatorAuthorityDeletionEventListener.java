package uk.gov.pmrv.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;

@RequiredArgsConstructor
@Component(value =  "accountRegulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final AccountCaSiteContactService accountCaSiteContactService;
    
    @Order(1)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorUserDeletedEvent(RegulatorAuthorityDeletionEvent event) {
        accountCaSiteContactService.removeUserFromCaSiteContact(event.getUserId());
    }

}
