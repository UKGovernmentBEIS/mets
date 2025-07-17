package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewRequestPeerReviewValidatorTest {

    @InjectMocks
    private PermitSurrenderReviewRequestPeerReviewValidator permitSurrenderReviewRequestPeerReviewValidator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private PermitSurrenderReviewDeterminationHandlerService permitSurrenderReviewDeterminationHandlerService;

    @Test
    void validate() {
        String peerReviewer = "peerReviewer";
        String userId = "userId";
        AppUser appUser = AppUser.builder().userId(userId).build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(peerReviewer)
            .build();
        PermitSurrenderApplicationReviewRequestTaskPayload requestTaskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
            .permitSurrender(PermitSurrender.builder().build())
            .reviewDetermination(PermitSurrenderReviewDeterminationDeemWithdraw.builder().build())
            .reviewDecision(PermitSurrenderReviewDecision.builder().build())
            .build();

        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .build();

        permitSurrenderReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW, peerReviewer, appUser);
        verify(permitSurrenderReviewDeterminationHandlerService, times(1))
            .validateReview(requestTaskPayload.getReviewDecision(), requestTaskPayload.getReviewDetermination());
    }
}