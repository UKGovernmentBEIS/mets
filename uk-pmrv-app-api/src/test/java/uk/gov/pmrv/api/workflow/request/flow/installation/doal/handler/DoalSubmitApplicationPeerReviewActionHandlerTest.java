package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalRequestPeerReviewValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitApplicationPeerReviewActionHandlerTest {

    @InjectMocks
    private DoalSubmitApplicationPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DoalRequestPeerReviewValidator doalRequestPeerReviewValidator;

    @Mock
    private DoalSubmitService doalSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .build();

        final String processTaskId = "processTaskId";
        final String requestId = "AEM";
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .request(Request.builder().id(requestId).build())
                .build();
        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.DOAL_SUBMIT_OUTCOME, DoalSubmitOutcome.PEER_REVIEW_REQUIRED);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.DOAL_REQUEST_PEER_REVIEW, user, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(doalRequestPeerReviewValidator, times(1)).validate(taskPayload, taskActionPayload, user);
        verify(doalSubmitService, times(1)).requestPeerReview(requestTask, selectedPeerReviewer, user);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.DOAL_REQUEST_PEER_REVIEW);
    }
}
