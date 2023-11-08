package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

@ExtendWith(MockitoExtension.class)
class NerReviewValidatorTest {

    @InjectMocks
    private NerReviewValidator validator;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;
    
    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void isReviewDeterminationValid_whenValid_thenTrue() {

        final NerProceedToAuthorityDetermination determination = NerProceedToAuthorityDetermination.builder()
            .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
            .preliminaryAllocations(new TreeSet<>())
            .build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.ADDITIONAL_DOCUMENTS,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .determination(determination)
                .build();

        final boolean valid = validator.isReviewDeterminationValid(determination, taskPayload.getReviewGroupDecisions());

        assertTrue(valid);
        verifyNoInteractions(allowanceAllocationValidator);
    }
    
    @Test
    void isReviewDeterminationValid_whenDuplicatePreliminaryAllocation_thenFalse() {

        final  Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(10).build(),
                PreliminaryAllocation.builder().year(Year.of(2023)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(20).build()
        );
        final NerProceedToAuthorityDetermination determination = NerProceedToAuthorityDetermination.builder()
            .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
            .preliminaryAllocations(new TreeSet<>(allocations))
            .build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.ADDITIONAL_DOCUMENTS,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .determination(determination)
                .build();

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(false);

        final boolean valid = validator.isReviewDeterminationValid(determination, taskPayload.getReviewGroupDecisions());

        assertFalse(valid);
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
    }
    
    @Test
    void isReviewDeterminationValid_whenMissingGroups_thenFalse() {

        final  Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2021)).subInstallationName(SubInstallationName.ALUMINIUM).build(),
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).build()
        );
        final NerProceedToAuthorityDetermination determination = NerProceedToAuthorityDetermination.builder()
            .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
            .preliminaryAllocations(new TreeSet<>(allocations))
            .build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build()
                ))
                .determination(determination)
                .build();
        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(true);

        final boolean valid = validator.isReviewDeterminationValid(determination, taskPayload.getReviewGroupDecisions());

        assertFalse(valid);
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
    }

    @Test
    void isReviewDeterminationValid_whenAmends_thenFalse() {

        final NerProceedToAuthorityDetermination determination = NerProceedToAuthorityDetermination.builder()
            .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
            .preliminaryAllocations(new TreeSet<>())
            .build();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED).build(),
                    NerReviewGroup.ADDITIONAL_DOCUMENTS,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .determination(determination)
                .build();

        final boolean valid = validator.isReviewDeterminationValid(determination, taskPayload.getReviewGroupDecisions());

        assertFalse(valid);
        verifyNoInteractions(allowanceAllocationValidator);
    }

    @Test
    void validatePeerReview_whenAmends_thenThrowException() {

        final  Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2021)).subInstallationName(SubInstallationName.ALUMINIUM).build(),
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).build()
        );
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final NerDeterminationType type = NerDeterminationType.PROCEED_TO_AUTHORITY;
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .determination(NerProceedToAuthorityDetermination.builder()
                        .type(type)
                        .preliminaryAllocations(new TreeSet<>(allocations))
                        .build()
                )
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED).build(),
                    NerReviewGroup.ADDITIONAL_DOCUMENTS,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .build();
        final BigDecimal paymentAmount = new BigDecimal(10);

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(true);

        final BusinessException businessException = assertThrows(
            BusinessException.class, () -> validator.validateReviewTaskPayload(taskPayload, paymentAmount)
        );

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(peerReviewerTaskAssignmentValidator, never()).validate(
            RequestTaskType.NER_APPLICATION_PEER_REVIEW,
            "peerReviewer",
            pmrvUser
        );
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
    }

    @Test
    void validatePeerReview_whenPaymentRefundedMissing_thenThrowException() {

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final NerDeterminationType type = NerDeterminationType.CLOSED;
        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .determination(NerEndedDetermination.builder().type(type).build())
                .reviewGroupDecisions(Map.of(
                    NerReviewGroup.NEW_ENTRANT_DATA_REPORT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.VERIFIER_OPINION_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.REJECTED).build(),
                    NerReviewGroup.MONITORING_METHODOLOGY_PLAN,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
                    NerReviewGroup.CONFIDENTIALITY_STATEMENT,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED).build(),
                    NerReviewGroup.ADDITIONAL_DOCUMENTS,
                    NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                ))
                .build();
        final BigDecimal paymentAmount = new BigDecimal(10);
        
        final BusinessException businessException = assertThrows(
            BusinessException.class, () -> validator.validateReviewTaskPayload(taskPayload, paymentAmount)
        );

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(peerReviewerTaskAssignmentValidator, never()).validate(
            RequestTaskType.NER_APPLICATION_PEER_REVIEW,
            "peerReviewer",
            pmrvUser
        );
        verifyNoInteractions(allowanceAllocationValidator);
    }

    @Test
    void validatePeerReview_whenValid_thenDoNothing() {

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer("peerReviewer")
            .build();


        validator.validatePeerReview(taskActionPayload, pmrvUser);

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(
            RequestTaskType.NER_APPLICATION_PEER_REVIEW,
            "peerReviewer",
            pmrvUser
        );
    }
}
