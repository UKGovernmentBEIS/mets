package uk.gov.pmrv.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.verifier.event.VerifierUserDisabledEvent;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;

@RequiredArgsConstructor
@Component(value =  "accountVerifierUserDisabledEventListener")
public class VerifierUserDisabledEventListener {

    private final AccountVbSiteContactService accountVbSiteContactService;
    
    @Order(1)
    @EventListener(VerifierUserDisabledEvent.class)
    public void onVerifierUserDisabledEvent(VerifierUserDisabledEvent event) {
        accountVbSiteContactService.removeUserFromVbSiteContact(event.getUserId());
    }
}
