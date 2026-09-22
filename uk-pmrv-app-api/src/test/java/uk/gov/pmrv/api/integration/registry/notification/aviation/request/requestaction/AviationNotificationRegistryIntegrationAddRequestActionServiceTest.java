package uk.gov.pmrv.api.integration.registry.notification.aviation.request.requestaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.notification.aviation.request.AviationNotificationRegistryEvent;
import uk.gov.pmrv.api.integration.registry.notification.common.RegistryNotificationType;
import uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction.NotificationRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationNotificationRegistryIntegrationAddRequestActionServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private AviationNotificationRegistryIntegrationAddRequestActionService service;

    @Test
    void addRequestAction() {
        String requestId = "REQ-1";
        Integer registryId = 12345;

        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .requestId(requestId)
                .registryNotificationType(RegistryNotificationType.EMP_ISSUANCE_DEEMED_WITHDRAWN_NOTIFICATION)
                .build();

        Request request = Request.builder().id(requestId).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.addRequestAction(event, registryId);

        verify(requestService).findRequestById(requestId);
        ArgumentCaptor<RequestActionPayload> captor = ArgumentCaptor.forClass(RequestActionPayload.class);

        verify(requestService).addSystemActionToRequest(
                eq(request),
                captor.capture(),
                eq(RequestActionType.NOTIFICATION_SENT_TO_REGISTRY)
        );

        assertThat(captor.getValue())
                .isInstanceOf(NotificationRegistryIntegrationRequestActionPayload.class);
    }
}