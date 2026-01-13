package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountUpdatedEventListener {

    private final InstallationAccountUpdatedNotifyRegistryService notifyRegistryService;

    @EventListener(InstallationAccountUpdatedRegistryEvent.class)
    public void handle(InstallationAccountUpdatedRegistryEvent event) {
        notifyRegistryService.notifyRegistry(event);
    }

}
