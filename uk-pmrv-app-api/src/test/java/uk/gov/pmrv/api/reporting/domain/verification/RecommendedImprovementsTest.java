package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RecommendedImprovementsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_are_there_recommended_improvements_false_then_valid() {
        final RecommendedImprovements recommendedImprovements = RecommendedImprovements.builder()
                .areThereRecommendedImprovements(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<RecommendedImprovements>> violations = validator.validate(recommendedImprovements);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_recommended_improvements_true_then_valid() {
        final RecommendedImprovements recommendedImprovements = RecommendedImprovements.builder()
                .areThereRecommendedImprovements(Boolean.TRUE)
                .recommendedImprovements(Set.of(
                        VerifierComment.builder()
                                .reference("E1")
                                .explanation("explanation")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<RecommendedImprovements>> violations = validator.validate(recommendedImprovements);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_recommended_improvements_false_then_invalid() {
        final RecommendedImprovements recommendedImprovements = RecommendedImprovements.builder()
                .areThereRecommendedImprovements(Boolean.FALSE)
                .recommendedImprovements(Set.of(
                        VerifierComment.builder()
                                .reference("E1")
                                .explanation("explanation")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<RecommendedImprovements>> violations = validator.validate(recommendedImprovements);

        assertEquals(1, violations.size());
    }

    @Test
    void when_are_there_recommended_improvements_true_then_invalid() {
        final RecommendedImprovements recommendedImprovements = RecommendedImprovements.builder()
                .areThereRecommendedImprovements(Boolean.TRUE)
                .recommendedImprovements(Set.of(
                        VerifierComment.builder()
                                .reference("")
                                .explanation("")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<RecommendedImprovements>> violations = validator.validate(recommendedImprovements);

        assertEquals(2, violations.size());
    }
    @Test
    void when_are_there_recommended_improvements_true_then_null_verifier_comment_invalid() {
        Set<VerifierComment> verifierComments = new LinkedHashSet<>();
        verifierComments.add(null);
        final RecommendedImprovements recommendedImprovements = RecommendedImprovements.builder()
                .areThereRecommendedImprovements(Boolean.TRUE)
                .recommendedImprovements(verifierComments)
                .build();

        final Set<ConstraintViolation<RecommendedImprovements>> violations = validator.validate(recommendedImprovements);

        assertEquals(1, violations.size());
    }
}
