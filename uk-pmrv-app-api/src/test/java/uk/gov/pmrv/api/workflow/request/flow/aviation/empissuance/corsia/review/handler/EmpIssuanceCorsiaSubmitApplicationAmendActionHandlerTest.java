package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaSubmitApplicationAmendActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaSubmitApplicationAmendActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        AppUser appUser = AppUser.builder().build();
        EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload =
            EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpCorsiaReviewService, times(1)).submitAmend(actionPayload, requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND);
    }
}
