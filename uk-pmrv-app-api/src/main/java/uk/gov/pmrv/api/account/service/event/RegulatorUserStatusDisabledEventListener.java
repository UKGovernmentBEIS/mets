package uk.gov.pmrv.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;

@RequiredArgsConstructor
@Component
public class RegulatorUserStatusDisabledEventListener {

private final AccountCaSiteContactService accountCaSiteContactService;
    
    @EventListener(RegulatorUserStatusDisabledEvent.class)
    public void onRegulatorUserStatusDisabledEvent(RegulatorUserStatusDisabledEvent event) {
        removeUserFromCaSiteContact(event.getUserId());
    }
    
    private void removeUserFromCaSiteContact(String userId) {
        accountCaSiteContactService.removeUserFromCaSiteContact(userId);
    }
}
