package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;

public class PermitReviewDecisionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validOperatorAmendsNeededModel() {
        PermitIssuanceReviewDecision permitChangesRequiredDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes")
                .requiredChanges(List.of(ReviewDecisionRequiredChange.builder().reason("change1").files(Set.of(UUID.randomUUID())).build())).build())
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitChangesRequiredDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void invalidOperatorAmendsNeededNoChangesRequired() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes").build())
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidOperatorAmendsNeededEmptyRequiredChanges() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes")
                .requiredChanges(List.of(new ReviewDecisionRequiredChange("change1", Collections.emptySet()),
                    new ReviewDecisionRequiredChange("", Collections.emptySet()))).build())
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void validAcceptedModel() {
        PermitVariationReviewDecision permitAcceptedVariationDecision = PermitVariationReviewDecision.builder()
            .type(ReviewDecisionType.ACCEPTED)
            .details(PermitAcceptedVariationDecisionDetails.builder().notes("notes").build())
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitAcceptedVariationDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void validRejectedModel() {
        PermitVariationReviewDecision permitRejectedDecision = PermitVariationReviewDecision.builder()
            .type(ReviewDecisionType.REJECTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitRejectedDecision);

        assertThat(violations.size()).isEqualTo(0);
    }
}
