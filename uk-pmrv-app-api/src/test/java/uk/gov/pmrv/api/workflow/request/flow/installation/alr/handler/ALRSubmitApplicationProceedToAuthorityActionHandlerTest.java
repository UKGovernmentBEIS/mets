package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRProceedToAuthorityValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitApplicationProceedToAuthorityActionHandlerTest {

    @InjectMocks
    private ALRSubmitApplicationProceedToAuthorityActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private ALRProceedToAuthorityValidator alrProceedToAuthorityValidator;

    @Mock
    private ALRSubmitService alrSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess_no_notice() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_REGULATOR_REVIEW_SUBMIT_APPLICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .payload(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder()
                                .determination(DoalProceedToAuthorityDetermination.builder().needsOfficialNotice(false).build())
                                .build())
                        .build())
                .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.ALR_REGULATOR_REVIEW_OUTCOME, ALROutcome.SUBMITTED.name(),
                BpmnProcessConstants.ALR_DETERMINATION, DoalDeterminationType.PROCEED_TO_AUTHORITY,
                BpmnProcessConstants.ALR_SEND_NOTICE, false);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.ALR_PROCEED_TO_AUTHORITY, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(alrProceedToAuthorityValidator, times(1)).validateComplete(requestTask);
        verify(alrSubmitService, times(1)).complete(requestTask);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void doProcess_with_notice() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_REGULATOR_REVIEW_SUBMIT_APPLICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .payload(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder()
                                .determination(DoalProceedToAuthorityDetermination.builder().needsOfficialNotice(true).build())
                                .build())
                        .build())
                .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.ALR_REGULATOR_REVIEW_OUTCOME, ALROutcome.SUBMITTED.name(),
                BpmnProcessConstants.ALR_DETERMINATION, DoalDeterminationType.PROCEED_TO_AUTHORITY,
                BpmnProcessConstants.ALR_SEND_NOTICE, true);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.ALR_PROCEED_TO_AUTHORITY, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(alrProceedToAuthorityValidator, times(1)).validateNotify(requestTask, decisionNotification, user);
        verify(alrSubmitService, times(1)).notifyOperator(requestTask, payload);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.ALR_PROCEED_TO_AUTHORITY);
    }
}
