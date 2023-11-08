package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.AuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerAuthorityResponseValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NerAuthorityResponseNotifyOperatorActionHandlerTest {

    @InjectMocks
    private NerAuthorityResponseNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerReviewValidator reviewValidator;

    @Mock
    private NerAuthorityResponseValidator authorityResponseValidator;

    @Mock
    private NerAuthorityResponseService authorityResponseService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        final NerAuthorityResponseRequestTaskPayload requestTaskPayload =
            NerAuthorityResponseRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.NER_AUTHORITY_RESPONSE_PAYLOAD)
                .authorityResponse(GrantAuthorityResponse.builder().type(AuthorityResponseType.VALID).build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.NER_AUTHORITY_RESPONSE)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.NER_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(authorityResponseValidator, times(1))
            .validateAuthorityResponse(requestTaskPayload);
        verify(reviewValidator, times(1))
            .validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        verify(authorityResponseService, times(1))
            .saveAuthorityDecisionNotification(requestTask, decisionNotification, pmrvUser);
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, "2",
                BpmnProcessConstants.AUTHORITY_RESPONSE, AuthorityResponseType.VALID
            )
        );
    }

    @Test
    void getTypes() {

        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.NER_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION
        );
    }
}
