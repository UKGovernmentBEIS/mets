package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.event.PermitGrantedEvent;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class PermitGrantedEventListener {

    private final PermitGrantedNotifyRegistryService permitGrantedNotifyRegistryService;

    @EventListener(PermitGrantedEvent.class)
    public void handle(PermitGrantedEvent event) {
        permitGrantedNotifyRegistryService.notifyRegistry(event);
    }
}
