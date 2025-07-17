package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitNotificationReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitNotificationValidatorService validator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("regulator")
                .build();

        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final PermitNotificationReviewDecision reviewDecision = PermitNotificationReviewDecision.builder()
                .type(PermitNotificationReviewDecisionType.ACCEPTED)
                .details(
                    PermitNotificationAcceptedDecisionDetails.builder()
                        .notes("notes")
                        .officialNotice("officialNotice")
                        .followUp(FollowUp.builder()
                            .followUpResponseRequired(false)
                            .build())
                        .build()
                )

                .build();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                PermitNotificationApplicationReviewRequestTaskPayload.builder()
                        .reviewDecision(reviewDecision)
                        .build();
        final String requestId = "requestId";
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .request(Request.builder().id(requestId).payload(PermitNotificationRequestPayload.builder().build()).build())
                .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION,
                appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotificationReviewDecision(reviewDecision);
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, appUser);
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(workflowService).completeTask(eq("processTaskId"), captor.capture());

        Map<String, Object> actualMap = captor.getValue();
        assertEquals(3, actualMap.size());
        assertEquals(PermitNotificationReviewDecisionType.ACCEPTED, actualMap.get(BpmnProcessConstants.REVIEW_DECISION_TYPE_OUTCOME));
        assertEquals(DeterminationType.GRANTED, actualMap.get(BpmnProcessConstants.REVIEW_DETERMINATION));
        assertEquals(ReviewOutcome.NOTIFY_OPERATOR, actualMap.get(BpmnProcessConstants.REVIEW_OUTCOME));

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getReviewDecision()).isEqualTo(reviewDecision);
        assertThat(requestPayload.getReviewDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
