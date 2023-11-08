package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthority;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalAuthorityResponseValidator;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandlerTest {

    @InjectMocks
    private DoalSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DoalAuthorityResponseValidator doalAuthorityResponseValidator;

    @Mock
    private DoalAuthorityResponseService doalAuthorityResponseService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        final Long requestTaskId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .authorityResponse(DoalGrantAuthorityResponse.builder()
                        .type(DoalAuthorityResponseType.VALID)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .payload(DoalAuthorityResponseRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.DOAL_AUTHORITY_RESPONSE_PAYLOAD)
                        .doalAuthority(doalAuthority)
                        .build())
                .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.DOAL_AUTHORITY_RESPONSE, DoalAuthorityResponseType.VALID);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(doalAuthorityResponseValidator, times(1))
                .validate(requestTask, doalAuthority, decisionNotification, user);
        verify(doalAuthorityResponseService, times(1)).authorityResponseNotifyOperator(requestTask, payload);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
