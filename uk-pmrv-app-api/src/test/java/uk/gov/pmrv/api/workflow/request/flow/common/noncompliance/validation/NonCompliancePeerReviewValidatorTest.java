package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@ExtendWith(MockitoExtension.class)
class NonCompliancePeerReviewValidatorTest {

    @InjectMocks
    private NonCompliancePeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validateDailyPenaltyNoticePeerReview() {

        final NonComplianceDailyPenaltyNoticeRequestTaskPayload requestTaskPayload =
            NonComplianceDailyPenaltyNoticeRequestTaskPayload.builder().build();
        final RequestTaskType requestTaskType =
            RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW;
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer("peerReviewer")
            .build();
        final AppUser appUser = AppUser.builder().build();

        validator.validateDailyPenaltyNoticePeerReview(requestTaskPayload,
            requestTaskType,
            taskActionPayload,
            appUser
        );

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(
            requestTaskType,
            "peerReviewer",
            appUser);
    }
}