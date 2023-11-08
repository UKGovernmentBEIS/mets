package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation.PermitVariationReviewRequestPeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewRequestPeerReviewValidatorTest {

    @InjectMocks
    private PermitVariationReviewRequestPeerReviewValidator permitReviewRequestPeerReviewValidator;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Mock
    private PermitGrantedValidatorService permitGrantedValidatorService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validate_determination_granted() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationGrantDetermination determination = PermitVariationGrantDetermination.builder()
            .activationDate(LocalDate.now())
            .reason("determination reason")
            .type(DeterminationType.GRANTED)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .type(PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();

        doNothing().when(peerReviewerTaskAssignmentValidator).validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(true);

        permitReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION);
        verify(permitGrantedValidatorService, times(1)).validatePermit(any());
    }

    @Test
    void validate_determination_deem_withdrawn() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationDeemedWithdrawnDetermination determination = PermitVariationDeemedWithdrawnDetermination.builder()
            .reason("determination reason")
            .type(DeterminationType.DEEMED_WITHDRAWN)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .type(PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();

        doNothing().when(peerReviewerTaskAssignmentValidator).validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(true);

        permitReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION);
    }

    @Test
    void validate_determination_rejected() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationRejectDetermination determination = PermitVariationRejectDetermination.builder()
            .reason("determination reason")
            .type(DeterminationType.REJECTED)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .type(PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();

        doNothing().when(peerReviewerTaskAssignmentValidator).validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(true);

        permitReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION);
        verifyNoInteractions(permitGrantedValidatorService);
    }

    @Test
    void validate_invalid_peer_reviewer() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationDeemedWithdrawnDetermination determination = PermitVariationDeemedWithdrawnDetermination.builder()
            .reason("determination reason")
            .type(DeterminationType.DEEMED_WITHDRAWN)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().build())
            .type(PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();


        doThrow(new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED))
            .when(peerReviewerTaskAssignmentValidator).validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> permitReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
        verifyNoInteractions(permitReviewDeterminationValidatorService);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
    }

    @Test
    void validate_invalid_determination() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationDeemedWithdrawnDetermination determination = PermitVariationDeemedWithdrawnDetermination.builder()
            .reason("determination reason")
            .type(DeterminationType.DEEMED_WITHDRAWN)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .type(PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();

        doNothing().when(peerReviewerTaskAssignmentValidator).validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        when(permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION)).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> permitReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(PERMIT_VARIATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, pmrvUser);
        verify(permitReviewDeterminationValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, requestTaskPayload, RequestType.PERMIT_VARIATION);
    }
}
