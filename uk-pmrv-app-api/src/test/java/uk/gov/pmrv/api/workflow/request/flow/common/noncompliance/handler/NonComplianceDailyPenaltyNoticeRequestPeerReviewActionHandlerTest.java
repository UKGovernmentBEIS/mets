package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceDailyPenaltyNoticeApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonCompliancePeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NonComplianceDailyPenaltyNoticeRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private NonComplianceDailyPenaltyNoticeRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonCompliancePeerReviewValidator validator;

    @Mock
    private NonComplianceDailyPenaltyNoticeApplyService applyService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        final NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload =
            NonComplianceDailyPenaltyNoticeRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").type(RequestType.NON_COMPLIANCE).build())
            .type(RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE)
            .payload(taskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW,
            appUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1))
            .validateDailyPenaltyNoticePeerReview(taskPayload,
                RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW,
                taskActionPayload,
                appUser);
        verify(applyService, times(1))
            .saveRequestPeerReviewAction(requestTask, selectedPeerReviewer);
        verify(requestService, times(1)).addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED,
            appUser.getUserId()
        );
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.PEER_REVIEW_REQUIRED,
                BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX,
                RequestCustomContext.NON_COMPLIANCE_DAILY_PENALTY_NOTICE.getCode()
            )
        );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW);
    }
}
