package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

@Service
@Validated
@RequiredArgsConstructor
public class NerReviewValidator {

    private final AllowanceAllocationValidator allowanceAllocationValidator;
    private final NerSubmitValidator nerSubmitValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validateReviewTaskPayload(@NotNull @Valid final NerApplicationReviewRequestTaskPayload taskPayload,
                                          @NotNull final BigDecimal paymentAmount) {
        
        final boolean determinationValid = taskPayload.getDetermination() != null &&
            this.isReviewDeterminationValid(taskPayload.getDetermination(), taskPayload.getReviewGroupDecisions());
        
        final boolean paymentRefundedInformationValid = this.isPaymentRefundedInformationValid(paymentAmount, taskPayload);
        if (!determinationValid || !paymentRefundedInformationValid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        
        nerSubmitValidator.validateSubmitTaskPayload(taskPayload);
        
    }

    public void validateAmendTaskPayload(@NotNull @Valid final NerApplicationAmendsSubmitRequestTaskPayload taskPayload) {
        
        nerSubmitValidator.validateSubmitTaskPayload(taskPayload);
    }

    public boolean isReviewDeterminationValid(final NerDetermination determination,
                                              final Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions) {

        final boolean isDeterminedToProceed = determination.getType() == NerDeterminationType.PROCEED_TO_AUTHORITY;
        if (!isDeterminedToProceed) {
            return true;
        }
        final boolean allGroupsReviewed = reviewGroupDecisions.keySet()
            .containsAll(List.of(NerReviewGroup.values()));
        
        final boolean allGroupsAcceptedOrRejected = reviewGroupDecisions.values().stream()
            .allMatch(d -> d.getType() == ReviewDecisionType.ACCEPTED || d.getType() == ReviewDecisionType.REJECTED);

        Set<PreliminaryAllocation> preliminaryAllocations =
                ((NerProceedToAuthorityDetermination) determination).getPreliminaryAllocations();
        final boolean areAllocationsValid = preliminaryAllocations.isEmpty() || allowanceAllocationValidator.isValid(preliminaryAllocations);

        return allGroupsReviewed && allGroupsAcceptedOrRejected && areAllocationsValid;
    }

    public void validatePeerReview(final PeerReviewRequestTaskActionPayload payload,
                                   final PmrvUser pmrvUser) {

        peerReviewerTaskAssignmentValidator.validate(
            RequestTaskType.NER_APPLICATION_PEER_REVIEW,
            payload.getPeerReviewer(), 
            pmrvUser
        );
    }

    public void validateReviewNotifyUsers(final RequestTask requestTask,
                                          final DecisionNotification decisionNotification,
                                          final PmrvUser pmrvUser) {

        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final NerDeterminationType type = taskPayload.getDetermination().getType();
        if (type == NerDeterminationType.PROCEED_TO_AUTHORITY) {
            final NerProceedToAuthorityDetermination determination =
                (NerProceedToAuthorityDetermination) taskPayload.getDetermination();
            final boolean sendOfficialNotice = determination.getSendOfficialNotice();
            if (!sendOfficialNotice) {
                throw new BusinessException(ErrorCode.FORM_VALIDATION);
            }
        }
        
        this.validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
    }

    public void validateNotifyUsers(final RequestTask requestTask,
                                    final DecisionNotification decisionNotification,
                                    final PmrvUser pmrvUser) {

        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateCompleteReview(@NotNull @Valid final NerApplicationReviewRequestTaskPayload taskPayload) {

        final NerDeterminationType type = taskPayload.getDetermination().getType();
        final boolean proceedToAuthority = type == NerDeterminationType.PROCEED_TO_AUTHORITY;
        if (!proceedToAuthority) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        final NerProceedToAuthorityDetermination determination =
            (NerProceedToAuthorityDetermination) taskPayload.getDetermination();
        final boolean sendOfficialNotice = determination.getSendOfficialNotice();
        if (sendOfficialNotice) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
    
    private boolean isPaymentRefundedInformationValid(final BigDecimal paymentAmount, 
                                                      final NerApplicationReviewRequestTaskPayload taskPayload) {

        final boolean isEndedDetermination = 
            taskPayload.getDetermination().getType() == NerDeterminationType.CLOSED ||
            taskPayload.getDetermination().getType() == NerDeterminationType.DEEMED_WITHDRAWN;
        if (!isEndedDetermination) {
            return true;
        }
        
        final boolean hasPayment = paymentAmount.compareTo(BigDecimal.ZERO) > 0;
        final NerEndedDetermination determination =
            (NerEndedDetermination) taskPayload.getDetermination();
        final Boolean paymentRefunded = determination.getPaymentRefunded();
        
        return (paymentRefunded != null) == hasPayment; 
    }
}
