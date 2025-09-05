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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRAuthorityResponseValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandlerTest {

    @InjectMocks
    private ALRSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private ALRAuthorityResponseValidator alrAuthorityResponseValidator;

    @Mock
    private ALRAuthorityResponseService alrAuthorityResponseService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final ALRApplicationAuthorityReviewOutcome authorityReviewOutcome = ALRApplicationAuthorityReviewOutcome.builder()
                .authorityResponse(ALRGrantAuthorityResponse
                        .builder().type(DoalAuthorityResponseType.VALID).build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .payload(ALRAuthorityResponseSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD)
                        .authorityReviewOutcome(authorityReviewOutcome)
                        .build())
                .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.ALR_AUTHORITY_RESPONSE, DoalAuthorityResponseType.VALID);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(alrAuthorityResponseValidator, times(1))
                .validate(requestTask, authorityReviewOutcome, decisionNotification, user);
        verify(alrAuthorityResponseService, times(1)).authorityResponseNotifyOperator(requestTask, payload);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
