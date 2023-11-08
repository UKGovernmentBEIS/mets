package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service.RequestAviationAerUkEtsSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkSubmitActionHandlerTest {

    @InjectMocks
    private AviationAerUkSubmitActionHandler aviationAerUkSubmitActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsSubmitService requestAviationAerUkEtsSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processId = "processId";
        String requestId = "requestId";
        Request request = Request.builder().id(requestId).build();
        RequestTask task = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .processTaskId(processId)
            .build();
        PmrvUser user = PmrvUser.builder().build();
        RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder()
            .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(task);

        // Invoke
        aviationAerUkSubmitActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION, user, payload);

        //assertions
        assertNotNull(request.getSubmissionDate());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestAviationAerUkEtsSubmitService, times(1)).sendToRegulator(task, user);
        verify(workflowService, times(1)).completeTask(processId,
            Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.AVIATION_AER_OUTCOME, AviationAerOutcome.REVIEW_REQUESTED));
    }

    @Test
    void getTypes() {
        assertThat(aviationAerUkSubmitActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION);
    }
}