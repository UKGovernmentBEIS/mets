package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRespondToRegulatorCommentsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirRespondToRegulatorCommentsSaveActionHandlerTest {

    @InjectMocks
    private VirRespondToRegulatorCommentsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private VirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Test
    void process() {
        final long taskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(taskId).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final VirSaveRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                VirSaveRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS, pmrvUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySaveAction(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS));
    }
}
