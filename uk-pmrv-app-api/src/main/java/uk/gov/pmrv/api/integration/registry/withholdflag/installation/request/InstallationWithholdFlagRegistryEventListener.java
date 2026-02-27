package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagRegistryEventListener {

    private final InstallationWithholdFlagNotifyRegistryService installationWithholdFlagNotifyRegistryService;

    @EventListener
    @Transactional
    public void handleWithholdFlagRegistryEvent(WithholdFlagRegistryEvent withholdFlagRegistryEvent) {
        installationWithholdFlagNotifyRegistryService.notifyRegistry(withholdFlagRegistryEvent);
    }
}
