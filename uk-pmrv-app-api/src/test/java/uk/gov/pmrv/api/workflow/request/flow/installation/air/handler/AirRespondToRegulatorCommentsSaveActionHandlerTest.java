package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirRespondToRegulatorCommentsService;

@ExtendWith(MockitoExtension.class)
class AirRespondToRegulatorCommentsSaveActionHandlerTest {

    @InjectMocks
    private AirRespondToRegulatorCommentsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AirRespondToRegulatorCommentsService respondToRegulatorCommentsService;

    @Test
    void process() {
        final long taskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(taskId).build();
        final AppUser appUser = AppUser.builder().build();
        final AirSaveRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                AirSaveRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(respondToRegulatorCommentsService, times(1)).applySaveAction(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS));
    }
}
