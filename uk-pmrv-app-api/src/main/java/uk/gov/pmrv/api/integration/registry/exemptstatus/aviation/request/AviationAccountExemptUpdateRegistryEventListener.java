package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptUpdateRegistryEventListener {

    private final AviationAccountExemptUpdateNotifyRegistryService aviationAccountExemptUpdateNotifyRegistryService;

    @EventListener(AviationAccountExemptFlagEvent.class)
    @Transactional
    public void handleAccountContactRegistryEvent(AviationAccountExemptFlagEvent event) {
        aviationAccountExemptUpdateNotifyRegistryService.notifyRegistry(event);
    }

}
