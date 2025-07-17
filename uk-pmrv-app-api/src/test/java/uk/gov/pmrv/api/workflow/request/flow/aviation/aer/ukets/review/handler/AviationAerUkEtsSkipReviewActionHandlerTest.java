package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsSkipReviewActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsSkipReviewActionHandler completeActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsReviewService aerUkEtsReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        AppUser user = AppUser.builder().build();
        RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder().verificationPerformed(false).build();
        Request request = Request.builder().id("REQ-ID").payload(requestPayload).build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .payload(requestTaskPayload)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        completeActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_UKETS_SKIP_REVIEW, user, taskActionEmptyPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aerUkEtsReviewService, times(1)).updateRequestPayloadWithSkipReviewOutcome(requestTask, user);
        verify(workflowService, times(1)).
            completeTask(processTaskId, Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.SKIPPED));
    }

    @Test
    void getTypes() {
        assertThat(completeActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SKIP_REVIEW);
    }
}