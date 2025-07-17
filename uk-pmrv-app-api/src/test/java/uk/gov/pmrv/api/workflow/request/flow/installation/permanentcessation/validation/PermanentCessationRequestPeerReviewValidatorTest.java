package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationRequestPeerReviewValidatorTest {

    @InjectMocks
    private PermanentCessationRequestPeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validate_shouldValidateSuccessfully() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .payloadType(RequestTaskActionPayloadType.PERMANENT_CESSATION_REQUEST_PEER_REVIEW_PAYLOAD)
                .build();


        validator.validate(taskActionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(
                RequestTaskType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);

    }
}
