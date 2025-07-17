package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRespondToRegulatorCommentsService;

@ExtendWith(MockitoExtension.class)
class AviationVirRespondToRegulatorCommentsSaveActionHandlerTest {

    @InjectMocks
    private AviationVirRespondToRegulatorCommentsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationVirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Test
    void process() {
        
        final long taskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(taskId).build();
        final AppUser appUser = AppUser.builder().build();
        final AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
            AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySaveAction(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS));
    }
}
