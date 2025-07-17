package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_VARIATION_PEER_REVIEW_REQUESTED;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation.PermitVariationReviewRequestPeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitVariationReviewRequestPeerReviewActionHandler requestPeerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationReviewService permitVariationReviewService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PermitVariationReviewRequestPeerReviewValidator permitVariationReviewRequestPeerReviewValidator;

    @Mock
    private RequestService requestService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitVariationDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        requestPeerReviewActionHandler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW,
            appUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationReviewRequestPeerReviewValidator, times(1))
            .validate(requestTask, taskActionPayload, appUser);
        verify(permitVariationReviewService, times(1))
            .saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, "userId");
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, PERMIT_VARIATION_PEER_REVIEW_REQUESTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Test
    void process_invalid_determination() {
        Long requestTaskId = 1L;
        AppUser appUser = AppUser.builder().build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitVariationDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION))
            .when(permitVariationReviewRequestPeerReviewValidator)
            .validate(requestTask, taskActionPayload, appUser);

        BusinessException be = assertThrows(BusinessException.class,
            () -> requestPeerReviewActionHandler.process(
                requestTaskId,
                RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW,
                appUser,
                taskActionPayload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationReviewRequestPeerReviewValidator, times(1))
            .validate(requestTask, taskActionPayload, appUser);
    }

    @Test
    void getTypes() {
        assertThat(requestPeerReviewActionHandler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW);
    }
}
