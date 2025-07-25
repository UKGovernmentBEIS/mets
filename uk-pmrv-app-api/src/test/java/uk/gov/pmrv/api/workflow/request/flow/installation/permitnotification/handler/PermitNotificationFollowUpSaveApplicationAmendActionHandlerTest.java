package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpSaveApplicationAmendActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpSaveApplicationAmendActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {

        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload taskActionPayload =
            PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD)
                .response("the response")
                .files(Set.of(file))
                .followUpSectionsCompleted(Map.of("section1", true))
                .reviewSectionsCompleted(Map.of("section2", true))
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder().build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND,
            appUser,
            taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());

        assertThat(((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload)requestTask.getPayload())
            .getFollowUpResponse()).isEqualTo("the response");
        assertThat(((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload)requestTask.getPayload())
            .getFollowUpFiles()).isEqualTo(Set.of(file));assertThat(((PermitNotificationFollowUpRequestTaskPayload)requestTask.getPayload()).getFollowUpResponse()).isEqualTo("the response");
        assertThat(((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload)requestTask.getPayload())
            .getFollowUpSectionsCompleted()).isEqualTo(Map.of("section1", true));
        assertThat(((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload)requestTask.getPayload())
            .getReviewSectionsCompleted()).isEqualTo(Map.of("section2", true));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND);
    }
}
