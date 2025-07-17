package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.hanlder;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.handler.AviationAerCorsiaRequestVerificationActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service.RequestAviationAerCorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaRequestVerificationActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaRequestVerificationActionHandler aviationAerCorsiaRequestVerificationActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerCorsiaSubmitService requestAviationAerCorsiaSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processId = "processId";
        String requestId = "requestId";
        RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .build();
        AppUser user = AppUser.builder().build();
        AviationAerApplicationRequestVerificationRequestTaskActionPayload taskActionPayload =
                AviationAerApplicationRequestVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_REQUEST_VERIFICATION_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(task);

        // Invoke
        aviationAerCorsiaRequestVerificationActionHandler
                .process(requestTaskId, RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION, user, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestAviationAerCorsiaSubmitService, times(1)).sendToVerifier(taskActionPayload, task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.AVIATION_AER_OUTCOME, AviationAerOutcome.VERIFICATION_REQUESTED));
    }

    @Test
    void getTypes() {
        assertThat(aviationAerCorsiaRequestVerificationActionHandler.getTypes())
                .containsOnly(RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION);
    }
}
