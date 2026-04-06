package uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class NotificationRegistryIntegrationAddRequestActionService
{
    private final RequestService requestService;

    public void addRequestAction(String requestId, RegulatorNoticeEvent regulatorNoticeEvent, FileInfoDTO fileInfoDTO) {

        Request request = requestService.findRequestById(requestId);

        NotificationRegistryIntegrationRequestActionPayload actionPayload =
                NotificationRegistryIntegrationRequestActionPayload.builder()
                        .notificationType(regulatorNoticeEvent.getType())
                        .registryId(Integer.valueOf(regulatorNoticeEvent.getRegistryId()))
                        .sentFile(fileInfoDTO)
                        .payloadType(RequestActionPayloadType.NOTIFICATION_REGISTRY_INTEGRATION_PAYLOAD)
                        .build();

        requestService.addSystemActionToRequest(request,
                actionPayload,
                RequestActionType.NOTIFICATION_SENT_TO_REGISTRY);

    }
}
