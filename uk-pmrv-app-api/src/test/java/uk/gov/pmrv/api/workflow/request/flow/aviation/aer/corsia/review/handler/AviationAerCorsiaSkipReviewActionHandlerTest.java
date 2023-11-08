package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

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
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.RequestAviationAerCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaSkipReviewActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaSkipReviewActionHandler completeActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerCorsiaReviewService corsiaReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser user = PmrvUser.builder().build();
        RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        AviationAerCorsiaRequestPayload
            requestPayload = AviationAerCorsiaRequestPayload.builder().verificationPerformed(false).build();
        Request request = Request.builder().id("REQ-ID").payload(requestPayload).build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
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

        completeActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_CORSIA_SKIP_REVIEW, user, taskActionEmptyPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(corsiaReviewService, times(1)).updateRequestPayloadWithSkipReviewOutcome(requestTask, user);
        verify(workflowService, times(1)).
            completeTask(processTaskId, Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.SKIPPED));
    }

    @Test
    void getTypes() {
        assertThat(completeActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_CORSIA_SKIP_REVIEW);
    }
}