package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRespondToRegulatorCommentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AviationVirRespondToRegulatorCommentsSubmitActionHandlerTest {

    @InjectMocks
    private AviationVirRespondToRegulatorCommentsSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationVirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final String requestId = "AEM-001";
        final long taskId = 1L;
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
            "A1", RegulatorImprovementResponse.builder().build()
        );
        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .request(Request.builder().id(requestId).build())
            .payload(
                AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                    .regulatorImprovementResponses(regulatorImprovementResponses)
                    .build())
            .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
            AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                .payloadType(
                    RequestTaskActionPayloadType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS, pmrvUser,
            actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySubmitAction(actionPayload, requestTask, pmrvUser);
        verify(workflowService, times(1)).sendEvent(requestId, BpmnProcessConstants.VIR_RESPONSE_COMMENT_SUBMITTED,
            new HashMap<>());
        verifyNoMoreInteractions(workflowService);
    }

    @Test
    void process_no_improvements() {
        
        final String requestId = "AEM-001";
        final String processId = "processId";
        final long taskId = 1L;
        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .request(Request.builder().id(requestId).build())
            .processTaskId(processId)
            .payload(
                AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                    .regulatorImprovementResponses(Map.of())
                    .build())
            .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
            AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                .payloadType(
                    RequestTaskActionPayloadType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS, pmrvUser,
            actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySubmitAction(actionPayload, requestTask, pmrvUser);
        verify(workflowService, times(1)).completeTask(processId);
        verifyNoMoreInteractions(workflowService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(
            List.of(RequestTaskActionType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS));
    }
}
