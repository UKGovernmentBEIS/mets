package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsSubmitApplicationAmendActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsSubmitApplicationAmendActionHandler submitApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsReviewService requestAviationAerUkEtsReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND;
        AppUser appUser = AppUser.builder().build();
        AviationAerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload =
            AviationAerSubmitApplicationAmendRequestTaskActionPayload.builder().build();

        String requestId = "REQ-ID";
        String processTaskId = "processTaskId";
        Request request = Request.builder().id(requestId).build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        submitApplicationAmendActionHandler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestAviationAerUkEtsReviewService, times(1))
            .sendAmendedAerToRegulator(taskActionPayload, requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.AVIATION_AER_OUTCOME, AviationAerOutcome.REVIEW_REQUESTED)
        );
    }

    @Test
    void getTypes() {
        assertThat(submitApplicationAmendActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND);
    }
}