package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveResponseRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpSaveResponseActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpSaveResponseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {

        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpSaveResponseRequestTaskActionPayload cancelPayload =
            PermitNotificationFollowUpSaveResponseRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD)
                .response("the response")
                .files(Set.of(file))
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(PermitNotificationFollowUpRequestTaskPayload.builder().build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE,
            appUser,
            cancelPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());

        assertThat(((PermitNotificationFollowUpRequestTaskPayload)requestTask.getPayload()).getFollowUpResponse()).isEqualTo("the response");
        assertThat(((PermitNotificationFollowUpRequestTaskPayload)requestTask.getPayload()).getFollowUpFiles()).isEqualTo(Set.of(file));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE);
    }
}
