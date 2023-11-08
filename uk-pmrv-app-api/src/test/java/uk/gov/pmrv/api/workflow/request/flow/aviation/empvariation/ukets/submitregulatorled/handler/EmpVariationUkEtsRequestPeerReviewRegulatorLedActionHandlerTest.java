package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsRegulatorLedPeerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator.EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsRequestPeerReviewRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsRequestPeerReviewRegulatorLedActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpVariationUkEtsRegulatorLedPeerReviewService empVariationUkEtsRegulatorLedPeerReviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator validator;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String peerReviewer = "peerReviewer";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED;
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("REQ-ID").build())
            .processTaskId("process-task-id")
            .build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload
        		.builder()
        		.peerReviewer(peerReviewer)
        		.build();
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, pmrvUser, taskActionPayload);

        verify(validator, times(1)).validate(requestTask, taskActionPayload, pmrvUser);
        verify(empVariationUkEtsRegulatorLedPeerReviewService, times(1))
            .saveRequestPeerReviewAction(requestTask, peerReviewer, pmrvUser.getUserId());
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, 
            		RequestActionType.EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.EMP_VARIATION_SUBMIT_OUTCOME, EmpVariationSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED);
    }
}
