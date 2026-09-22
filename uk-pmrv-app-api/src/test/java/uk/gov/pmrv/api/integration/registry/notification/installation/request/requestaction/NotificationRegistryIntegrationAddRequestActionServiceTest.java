package uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.ReturnOfAllowancesRegulatorNoticeEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationRegistryIntegrationAddRequestActionServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private NotificationRegistryIntegrationAddRequestActionService service;

    @Test
    void addRequestAction_forRegularEvent() {
        String requestId = "REQ-1";
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid("de305d54-75b4-431b-adb2-eb6b9e546014")
                .name("notification.pdf")
                .build();

        RegulatorNoticeEvent event = RegulatorNoticeEvent.builder()
                .registryId("12345")
                .type("SURRENDER_NOTICE")
                .build();

        Request request = Request.builder().id(requestId).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.addRequestAction(requestId, event, fileInfoDTO);

        ArgumentCaptor<RequestActionPayload> payloadCaptor = ArgumentCaptor.forClass(RequestActionPayload.class);

        verify(requestService).findRequestById(requestId);
        verify(requestService).addSystemActionToRequest(
                eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.NOTIFICATION_SENT_TO_REGISTRY)
        );

        assertThat(payloadCaptor.getValue()).isInstanceOf(NotificationRegistryIntegrationRequestActionPayload.class);

        NotificationRegistryIntegrationRequestActionPayload payload =
                (NotificationRegistryIntegrationRequestActionPayload) payloadCaptor.getValue();

        assertThat(payload.getRegistryId()).isEqualTo(12345);
        assertThat(payload.getNotificationType()).isEqualTo("SURRENDER_NOTICE");
        assertThat(payload.getSentFile()).isEqualTo(fileInfoDTO);
        assertThat(payload.getPayloadType()).isEqualTo(RequestActionPayloadType.NOTIFICATION_REGISTRY_INTEGRATION_PAYLOAD);
    }

    @Test
    void addRequestAction_forReturnOfAllowancesEvent() {
        String requestId = "REQ-2";
        LocalDate returnDate = LocalDate.of(2026, 1, 31);

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid("de305d54-75b4-431b-adb2-eb6b9e546014")
                .name("return_of_allowances.pdf")
                .build();

        ReturnOfAllowancesRegulatorNoticeEvent event = ReturnOfAllowancesRegulatorNoticeEvent.builder()
                .registryId("56789")
                .type("RETURN_OF_ALLOWANCES")
                .returnDate(returnDate)
                .build();

        Request request = Request.builder().id(requestId).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.addRequestAction(requestId, event, fileInfoDTO);

        ArgumentCaptor<RequestActionPayload> payloadCaptor = ArgumentCaptor.forClass(RequestActionPayload.class);

        verify(requestService).findRequestById(requestId);
        verify(requestService).addSystemActionToRequest(
                eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.RETURN_OF_ALLOWANCES_NOTIFICATION_SENT_TO_REGISTRY)
        );

        assertThat(payloadCaptor.getValue())
                .isInstanceOf(ReturnOfAllowancesNotificationRegistryIntegrationRequestActionPayload.class);

        ReturnOfAllowancesNotificationRegistryIntegrationRequestActionPayload payload =
                (ReturnOfAllowancesNotificationRegistryIntegrationRequestActionPayload) payloadCaptor.getValue();

        assertThat(payload.getRegistryId()).isEqualTo(56789);
        assertThat(payload.getNotificationType()).isEqualTo("RETURN_OF_ALLOWANCES");
        assertThat(payload.getSentFile()).isEqualTo(fileInfoDTO);
        assertThat(payload.getReturnOfAllowancesDate()).isEqualTo(returnDate);
        assertThat(payload.getPayloadType()).isEqualTo(
                RequestActionPayloadType.RETURN_OF_ALLOWANCES_NOTIFICATION_REGISTRY_INTEGRATION_PAYLOAD
        );
    }
}