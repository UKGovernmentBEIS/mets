package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSkipReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSkipReviewActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSkipReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AerSkipReviewActionHandlerTest {

    @InjectMocks
    private AerSkipReviewActionHandler completeActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AerReviewService aerReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        AppUser user = AppUser.builder().build();
        AerApplicationSkipReviewRequestTaskActionPayload aerSkipReviewPayload = AerApplicationSkipReviewRequestTaskActionPayload.builder()
                .aerSkipReviewDecision(AerSkipReviewDecision.builder().type(AerSkipReviewActionType.OTHER).reason("Test").build())
                .build();

        AerRequestPayload
                requestPayload = AerRequestPayload.builder().verificationPerformed(false).build();
        Request request = Request.builder().id("REQ-ID").payload(requestPayload).build();

        Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        Aer aer = Aer.builder().build();
        AerApplicationReviewRequestTaskPayload requestTaskPayload =
                AerApplicationReviewRequestTaskPayload.builder()
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

        completeActionHandler.process(requestTaskId, RequestTaskActionType.AER_SKIP_REVIEW, user, aerSkipReviewPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aerReviewService, times(1)).updateRequestPayloadWithSkipReviewOutcome(requestTask, aerSkipReviewPayload, user);
        verify(workflowService, times(1)).
                completeTask(processTaskId, Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AER_REVIEW_OUTCOME, ReviewOutcome.SKIPPED));
    }

    @Test
    void getTypes() {
        assertThat(completeActionHandler.getTypes()).containsOnly(RequestTaskActionType.AER_SKIP_REVIEW);
    }
}
