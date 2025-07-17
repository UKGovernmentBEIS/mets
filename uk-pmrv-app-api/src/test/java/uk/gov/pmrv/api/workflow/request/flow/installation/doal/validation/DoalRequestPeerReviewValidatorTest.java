package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DoalRequestPeerReviewValidatorTest {

    @InjectMocks
    private DoalRequestPeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private DoalSubmitValidator doalSubmitValidator;

    @Test
    void validate() {
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .build();
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.DOAL_REQUEST_PEER_REVIEW_PAYLOAD)
                .peerReviewer(selectedPeerReviewer)
                .build();
        final AppUser appUser = AppUser.builder().userId("userId").build();

        // Invoke
        validator.validate(taskPayload, taskActionPayload, appUser);

        // Verify
        verify(peerReviewerTaskAssignmentValidator, times(1))
                .validate(RequestTaskType.DOAL_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(doalSubmitValidator, times(1)).validate(taskPayload);
    }
}
