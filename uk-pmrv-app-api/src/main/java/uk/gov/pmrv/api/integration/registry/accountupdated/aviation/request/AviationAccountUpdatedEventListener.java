package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountUpdatedEventListener {

    private final AviationAccountUpdatedNotifyRegistryService notifyRegistryService;

    @EventListener(AviationAccountUpdatedRegistryEvent.class)
    public void handle(AviationAccountUpdatedRegistryEvent event) {
        notifyRegistryService.notifyRegistry(event);
    }
}
