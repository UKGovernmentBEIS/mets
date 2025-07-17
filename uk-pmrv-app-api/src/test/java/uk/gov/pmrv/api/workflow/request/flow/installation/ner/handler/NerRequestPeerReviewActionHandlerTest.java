package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NerRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private NerRequestPeerReviewActionHandler requestPeerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerReviewValidator reviewValidator;

    @Mock
    private RequestService requestService;

    @Mock
    private NerApplyReviewService applyReviewService;

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
        final NerApplicationReviewRequestTaskPayload requestTaskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_REVIEW_PAYLOAD)
            .determination(NerEndedDetermination.builder().type(NerDeterminationType.CLOSED).build())
            .build();
        final BigDecimal paymentAmount = new BigDecimal(100);
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").payload(NerRequestPayload.builder().paymentAmount(paymentAmount).build()).build())
            .type(RequestTaskType.NER_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        requestPeerReviewActionHandler.process(
            requestTaskId,
            RequestTaskActionType.NER_REQUEST_PEER_REVIEW,
            appUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(reviewValidator, times(1))
            .validateReviewTaskPayload(requestTaskPayload, paymentAmount);
        verify(reviewValidator, times(1))
            .validatePeerReview(taskActionPayload, appUser);
        verify(applyReviewService, times(1)).saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, appUser);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, RequestActionType.NER_PEER_REVIEW_REQUESTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, NerSubmitOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void process_invalid_determination() {

        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        final NerApplicationReviewRequestTaskPayload requestTaskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_REVIEW_PAYLOAD)
            .determination(NerEndedDetermination.builder().type(NerDeterminationType.CLOSED).build())
            .build();
        final BigDecimal paymentAmount = new BigDecimal(100);
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").payload(NerRequestPayload.builder().paymentAmount(paymentAmount).build()).build())
            .type(RequestTaskType.NER_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION))
            .when(reviewValidator)
            .validatePeerReview(taskActionPayload, appUser);

        final BusinessException be = assertThrows(BusinessException.class,
            () -> requestPeerReviewActionHandler.process(
                requestTaskId,
                RequestTaskActionType.NER_REQUEST_PEER_REVIEW,
                appUser,
                taskActionPayload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(reviewValidator, times(1))
            .validatePeerReview(taskActionPayload, appUser);
    }

    @Test
    void getTypes() {
        assertThat(requestPeerReviewActionHandler.getTypes()).containsExactly(RequestTaskActionType.NER_REQUEST_PEER_REVIEW);
    }
}
