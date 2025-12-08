package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountCreatedEventListener {

    private final InstallationAccountCreatedNotifyRegistryService installationAccountCreatedNotifyRegistryService;

    @EventListener(InstallationAccountCreatedRegistryEvent.class)
    public void handle(InstallationAccountCreatedRegistryEvent event) {
        installationAccountCreatedNotifyRegistryService.notifyRegistry(event);
    }
}
