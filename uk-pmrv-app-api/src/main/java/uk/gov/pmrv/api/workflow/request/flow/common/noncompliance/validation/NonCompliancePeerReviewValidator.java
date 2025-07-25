package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@Validated
@RequiredArgsConstructor
public class NonCompliancePeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validateDailyPenaltyNoticePeerReview(
        @Valid @SuppressWarnings("unused") final NonComplianceDailyPenaltyNoticeRequestTaskPayload requestTaskPayload,
        final RequestTaskType peerReviewRequestTaskType,
        final PeerReviewRequestTaskActionPayload payload,
        final AppUser appUser
    ) {
        peerReviewerTaskAssignmentValidator.validate(peerReviewRequestTaskType, payload.getPeerReviewer(), appUser);
    }

    public void validateNoticeOfIntentPeerReview(
        @Valid @SuppressWarnings("unused") final NonComplianceNoticeOfIntentRequestTaskPayload requestTaskPayload,
        final RequestTaskType peerReviewRequestTaskType,
        final PeerReviewRequestTaskActionPayload payload,
        final AppUser appUser
    ) {
        peerReviewerTaskAssignmentValidator.validate(peerReviewRequestTaskType, payload.getPeerReviewer(), appUser);
    }

    public void validateCivilPenaltyPeerReview(
        @Valid @SuppressWarnings("unused") final NonComplianceCivilPenaltyRequestTaskPayload requestTaskPayload,
        final RequestTaskType peerReviewRequestTaskType,
        final PeerReviewRequestTaskActionPayload payload,
        final AppUser appUser
    ) {
        peerReviewerTaskAssignmentValidator.validate(peerReviewRequestTaskType, payload.getPeerReviewer(), appUser);
    }
}
