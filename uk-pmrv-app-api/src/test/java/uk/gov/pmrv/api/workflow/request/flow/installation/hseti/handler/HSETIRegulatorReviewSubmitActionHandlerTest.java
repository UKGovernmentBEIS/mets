package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import org.junit.jupiter.api.Assertions;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIRegulatorReviewSubmitActionHandlerTest {


    @InjectMocks
    private HSETIRegulatorReviewSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private HSETIRegulatorReviewSubmitService submitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator"))
                .signatory("signatory").build();

        final NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload
                .builder()
                .decisionNotification(decisionNotification)
                .payloadType(RequestTaskActionPayloadType.HSE_TI_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .build();
        final String processId = "processId";
        final String requestId = "requestId";

        final HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .overallDecision(HSETIRegulatorReviewOverallDecision.builder().type(HSETIRegulatorReviewOverallDecisionType.APPROVED).build())
                .build();

        final Request request = Request.builder().id(requestId).payload(requestPayload).build();

        final RequestTask task = RequestTask.builder()
                .request(request)
                .type(RequestTaskType.HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT)
                .processTaskId(processId)
                .build();


        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.HSE_TI_REGULATOR_REVIEW_SUBMIT, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(submitService, times(1)).submit(task, decisionNotification, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, task.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, HSETIRegulatorReviewOverallDecisionType.APPROVED,
                BpmnProcessConstants.HSE_TI_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.HSE_TI_REGULATOR_REVIEW_SUBMIT), handler.getTypes());
    }
}
