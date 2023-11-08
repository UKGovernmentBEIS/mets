package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitTransferBReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitReviewNotifyOperatorValidator validator;

    @Mock
    private PermitTransferBReviewService permitTransferBReviewService;
    
    @Mock
    private WorkflowService workflowService;


    @Test
    void doProcess() {

        final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload payload =
            PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .build();

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(PermitTransferBApplicationReviewRequestTaskPayload.builder()
                .determination(PermitIssuanceRejectDetermination.builder()
                    .type(DeterminationType.REJECTED).build()).build())
            .request(Request.builder().id("2").build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(), 
                        RequestTaskActionType.PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION, 
                        pmrvUser,
                        payload);

        verify(validator, times(1)).validate(requestTask, payload, pmrvUser);
        verify(permitTransferBReviewService, times(1)).savePermitTransferBDecisionNotification(requestTask, payload.getDecisionNotification(), pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationType.REJECTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }
}
