package uk.gov.pmrv.api.integration.registry.notification.aviation.request.requestaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.integration.registry.notification.common.NotificationRegistryEvent;
import uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction.NotificationRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class AviationNotificationRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(NotificationRegistryEvent notificationRegistryEvent, Integer registryId) {

        Request request = requestService.findRequestById(notificationRegistryEvent.getRequestId());
        RequestActionPayload payload = buildPayload(notificationRegistryEvent, registryId);

        requestService.addSystemActionToRequest(
                request,
                payload,
                RequestActionType.NOTIFICATION_SENT_TO_REGISTRY
        );
    }

    private RequestActionPayload buildPayload(
            NotificationRegistryEvent notificationRegistryEvent,
            Integer registryId) {

        return switch (notificationRegistryEvent.getRegistryNotificationType()) {
            case AVIATION_ACCOUNT_CLOSED ->
                    NotificationRegistryIntegrationNoFileRequestActionPayload.builder()
                            .payloadType(RequestActionPayloadType.NOTIFICATION_REGISTRY_INTEGRATION_NO_FILE_PAYLOAD)
                            .registryId(registryId)
                            .notificationType(notificationRegistryEvent.getRegistryNotificationType().getName())
                            .build();

            default ->
                    NotificationRegistryIntegrationRequestActionPayload.builder()
                            .payloadType(RequestActionPayloadType.NOTIFICATION_REGISTRY_INTEGRATION_PAYLOAD)
                            .registryId(registryId)
                            .notificationType(notificationRegistryEvent.getRegistryNotificationType().getName())
                            .sentFile(notificationRegistryEvent.getFileInfoDTO())
                            .build();
        };
    }

}
