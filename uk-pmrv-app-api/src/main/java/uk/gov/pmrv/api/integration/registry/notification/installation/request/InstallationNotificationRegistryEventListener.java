package uk.gov.pmrv.api.integration.registry.notification.installation.request;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationNotificationRegistryEventListener {

    private final InstallationNotificationNotifyRegistryService installationNotificationNotifyRegistryService;

    @EventListener
    @Transactional
    public void handleNotificationRegistryEvent(NotificationRegistryEvent notificationRegistryEvent) {
        installationNotificationNotifyRegistryService.notifyRegistry(notificationRegistryEvent);
    }

}
