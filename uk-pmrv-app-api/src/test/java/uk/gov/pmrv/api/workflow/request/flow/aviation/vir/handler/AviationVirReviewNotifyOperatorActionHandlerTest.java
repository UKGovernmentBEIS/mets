package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirReviewNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

@ExtendWith(MockitoExtension.class)
class AviationVirReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AviationVirReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationVirReviewService virReviewService;

    @Mock
    private VirReviewNotifyOperatorValidator virReviewNotifyOperatorValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String processId = "processId";
        final String requestId = "VIR001";

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload actionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.VIR_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(VirRequestPayload.builder()
                        .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processId)
                .request(request)
                .payload(AviationVirApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD)
                        .regulatorReviewResponse(RegulatorReviewResponse.builder()
                                .regulatorImprovementResponses(Map.of(
                                        "A1", RegulatorImprovementResponse.builder().improvementRequired(true).build()
                                ))
                                .build())
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(virReviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, appUser);
        verify(virReviewService, times(1))
                .submitReview(requestTask, decisionNotification, appUser);
        verify(workflowService, times(1))
                .completeTask(processId, Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.COMPLETED,
                        BpmnProcessConstants.VIR_NEEDS_IMPROVEMENTS, true));
    }
}
