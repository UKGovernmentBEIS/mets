package uk.gov.pmrv.api.integration.registry.notification.aviation.request;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class AviationNotificationRegistryEventListener {

    private final AviationNotificationNotifyRegistryService aviationNotificationNotifyRegistryService;

    @EventListener
    @Transactional
    public void handleNotificationRegistryEvent(AviationNotificationRegistryEvent notificationRegistryEvent) {
        aviationNotificationNotifyRegistryService.notifyRegistry(notificationRegistryEvent);
    }

}
