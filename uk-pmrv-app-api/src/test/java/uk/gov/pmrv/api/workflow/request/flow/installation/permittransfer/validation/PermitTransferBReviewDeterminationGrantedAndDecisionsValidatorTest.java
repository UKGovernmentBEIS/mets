package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationGrantedAndDecisionsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewGroupsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.validation.PermitTransferBReviewDeterminationGrantedAndDecisionsValidator;

@ExtendWith(MockitoExtension.class)
class PermitTransferBReviewDeterminationGrantedAndDecisionsValidatorTest {

    @InjectMocks
    private PermitTransferBReviewDeterminationGrantedAndDecisionsValidator validator;

    @Mock
    private PermitReviewDeterminationGrantedAndDecisionsValidator<PermitIssuanceReviewDecision> permitReviewDeterminationGrantedAndDecisionsValidator;

    @Mock
    private PermitReviewGroupsValidator<PermitIssuanceReviewDecision> permitReviewGroupsValidator;

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(DeterminationType.GRANTED);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(RequestType.PERMIT_TRANSFER_B);
    }

    @Test
    void isValid_true() {
        
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
                PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
            ))
            .permitTransferDetailsConfirmationDecision(
                PermitTransferBDetailsConfirmationReviewDecision.builder().type(
                    PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED).build())
            .build();

        when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(true);

        assertThat(validator.isValid(taskPayload)).isTrue();

        verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }

    @Test
    void isValid_false_not_contains_all_mandatory_groups() {
        
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
                PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
            ))
            .permitTransferDetailsConfirmationDecision(PermitTransferBDetailsConfirmationReviewDecision.builder()
                .type(PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED).build())
            .build();

        when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(false);

        assertThat(validator.isValid(taskPayload)).isFalse();

        verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
        verifyNoInteractions(permitReviewDeterminationGrantedAndDecisionsValidator);
    }

    @Test
    void isValid_false_not_contains_transfer_details_decision() {

        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
                PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
            ))
            .build();

        when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);

        assertThat(validator.isValid(taskPayload)).isFalse();

        verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
        verifyNoInteractions(permitReviewDeterminationGrantedAndDecisionsValidator);
    }

    @Test
    void isValid_false_granted_determination_not_valid() {
        
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
                PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
            ))
            .permitTransferDetailsConfirmationDecision(
                PermitTransferBDetailsConfirmationReviewDecision.builder()
                    .type(PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED).build())
            .build();

        when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(false);

        assertThat(validator.isValid(taskPayload)).isFalse();

        verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }

    @Test
    void isValid_false_transfer_details_is_not_accepted() {

        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.MONITORING_METHODOLOGY_PLAN,
                PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
            ))
            .permitTransferDetailsConfirmationDecision(
                PermitTransferBDetailsConfirmationReviewDecision.builder()
                    .type(PermitTransferBDetailsConfirmationReviewDecisionType.REJECTED).build())
            .build();

        when(permitReviewGroupsValidator.containsDecisionForAllPermitGroups(taskPayload)).thenReturn(true);
        when(permitReviewDeterminationGrantedAndDecisionsValidator.isValid(taskPayload)).thenReturn(true);

        assertThat(validator.isValid(taskPayload)).isFalse();

        verify(permitReviewGroupsValidator, times(1)).containsDecisionForAllPermitGroups(taskPayload);
        verify(permitReviewDeterminationGrantedAndDecisionsValidator, times(1)).isValid(taskPayload);
    }

}
