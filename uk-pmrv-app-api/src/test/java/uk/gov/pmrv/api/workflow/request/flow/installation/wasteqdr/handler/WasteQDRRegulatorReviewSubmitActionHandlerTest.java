package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecisionType.ACCEPTED;

@ExtendWith(MockitoExtension.class)
public class WasteQDRRegulatorReviewSubmitActionHandlerTest {

    @InjectMocks
    private WasteQDRRegulatorReviewSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private WasteQDRRegulatorReviewSubmitService submitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final String processId = "processId";
        final String requestId = "requestId";

        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory("sign").build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.WASTE_QDR_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();
        WasteQDRReviewAcceptedDecisionDetails decisionDetails = WasteQDRReviewAcceptedDecisionDetails
                .builder()
                .notes("notes")
                .build();
        WasteQDRReviewDecision reviewDecision = WasteQDRReviewDecision.builder()
                .type(ACCEPTED)
                .details(decisionDetails)
                .build();
        WasteQDRRequestPayload wasteQDRRequestPayload = WasteQDRRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .reviewDecision(reviewDecision)
                .build();

        final RequestTask task = RequestTask.builder()
                .request(Request.builder()
                        .id(requestId)
                        .payload(wasteQDRRequestPayload)
                        .build())
                .type(RequestTaskType.WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT)
                .processTaskId(processId)
                .build();


        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.WASTE_QDR_REGULATOR_REVIEW_SUBMIT, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(submitService, times(1)).submit(task, user);
        verify(workflowService, times(1)).completeTask(
                processId,
                Map.of(
                        BpmnProcessConstants.REQUEST_ID, task.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, ACCEPTED,
                        BpmnProcessConstants.WASTE_QDR_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.COMPLETED
                )
        );
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.WASTE_QDR_REGULATOR_REVIEW_SUBMIT), handler.getTypes());
    }
}
