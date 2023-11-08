package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirReviewNotifyOperatorValidator;

@ExtendWith(MockitoExtension.class)
class AirReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AirReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AirReviewService reviewService;

    @Mock
    private AirReviewNotifyOperatorValidator reviewNotifyOperatorValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String processId = "processId";
        final String requestId = "AIR001";

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(AirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processId)
                .request(request)
                .payload(AirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(Map.of(
                                        1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
                                )).build())
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.AIR_NOTIFY_OPERATOR_FOR_DECISION, pmrvUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(reviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, pmrvUser);
        verify(reviewService, times(1))
                .submitReview(requestTask, decisionNotification, pmrvUser);
        verify(workflowService, times(1))
                .completeTask(processId, Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.COMPLETED,
                        BpmnProcessConstants.AIR_NEEDS_IMPROVEMENTS, true));
    }
}
