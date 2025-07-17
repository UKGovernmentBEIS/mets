package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRespondToRegulatorCommentsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirRespondToRegulatorCommentsSubmitActionHandlerTest {

    @InjectMocks
    private VirRespondToRegulatorCommentsSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private VirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

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
                        VirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                                .payloadType(RequestTaskPayloadType.VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                                .regulatorImprovementResponses(regulatorImprovementResponses)
                                .build())
                .build();
        final AppUser appUser = AppUser.builder().build();
        final VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySubmitAction(actionPayload, requestTask, appUser);
        verify(workflowService, times(1)).sendEvent(requestId, BpmnProcessConstants.VIR_RESPONSE_COMMENT_SUBMITTED, new HashMap<>());
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
                        VirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                                .payloadType(RequestTaskPayloadType.VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                                .regulatorImprovementResponses(Map.of())
                                .build())
                .build();
        final AppUser appUser = AppUser.builder().build();
        final VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(virRespondToRegulatorCommentsService, times(1)).applySubmitAction(actionPayload, requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processId);
        verifyNoMoreInteractions(workflowService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS));
    }
}
