package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.enumeration.AviationAerCorsia3YearPeriodOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation.AviationAerCorsia3YearPeriodOffsettingValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingRequestPeerReviewActionHandlerTest {


    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingSubmitService submitService;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingValidator validator;

    @Test
    void process() {
        String requestId = "1";
        Long requestTaskId = 2L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PEER_REVIEW;
        AppUser appUser = AppUser.builder().userId("user").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PEER_REVIEW_PAYLOAD)
                .peerReviewer("reg2")
                .build();
        Request request = Request.builder().id(requestId).type(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING).build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1)).validatePeerReviewRequest(requestTask, taskActionPayload, appUser);
        verify(submitService, times(1)).requestPeerReview(requestTask, "reg2", appUser);
        verify(requestService, times(1))
                .addActionToRequest(request, null, RequestActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED, appUser.getUserId());

        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.PEER_REVIEW_REQUIRED));
    }


    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PEER_REVIEW);
    }

}
