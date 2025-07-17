package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRRequestPeerReviewValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationPeerReviewActionHandlerTest {

    @InjectMocks
    private BDRApplicationPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRRequestPeerReviewValidator validator;

    @Mock
    private RequestService requestService;

    @Mock
    private BDRSubmitService bdrSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .payloadType(RequestTaskActionPayloadType.BDR_REQUEST_PEER_REVIEW_PAYLOAD)
                .build();
        final Request request = Request.builder().id("2").build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId("processTaskId")
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
                requestTaskId,
                RequestTaskActionType.BDR_REQUEST_PEER_REVIEW,
                appUser,
                taskActionPayload);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(validator).validate(requestTask, taskActionPayload, appUser);
        verify(requestService).addActionToRequest(request, null, RequestActionType.BDR_APPLICATION_PEER_REVIEW_REQUESTED, appUser.getUserId());
        verify(workflowService).completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.BDR_REGULATOR_REVIEW_OUTCOME, BDROutcome.PEER_REVIEW_REQUIRED
        ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDR_REQUEST_PEER_REVIEW);
    }
}
