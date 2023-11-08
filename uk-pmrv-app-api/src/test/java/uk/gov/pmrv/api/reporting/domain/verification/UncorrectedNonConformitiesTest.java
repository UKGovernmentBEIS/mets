package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UncorrectedNonConformitiesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_are_there_uncorrected_non_conformities_false_then_valid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .areTherePriorYearIssues(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_conformities_true_then_valid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.TRUE)
                .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder()
                                .reference("B1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .areTherePriorYearIssues(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_conformities_false_then_invalid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder()
                                .reference("B1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .areTherePriorYearIssues(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(1, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_conformities_true_then_invalid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.TRUE)
                .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder()
                                .reference("")
                                .explanation("")
                                .build()
                ))
                .areTherePriorYearIssues(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(3, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_conformities_true_then_null_uncorrected_non_conformities_invalid() {
        Set<UncorrectedItem> uncorrectedNonConformitiesSet = new LinkedHashSet<>();
        uncorrectedNonConformitiesSet.add(null);
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.TRUE)
                .uncorrectedNonConformities(uncorrectedNonConformitiesSet)
                .areTherePriorYearIssues(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(1, violations.size());
    }


    @Test
    void when_are_there_prior_year_issues_true_then_valid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .areTherePriorYearIssues(Boolean.TRUE)
                .priorYearIssues(Set.of(
                        VerifierComment.builder()
                                .reference("C1")
                                .explanation("explanation")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_prior_year_issues_false_then_invalid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .areTherePriorYearIssues(Boolean.FALSE)
                .priorYearIssues(Set.of(
                        VerifierComment.builder()
                                .reference("C1")
                                .explanation("explanation")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(1, violations.size());
    }

    @Test
    void when_are_there_prior_year_issues_true_then_invalid() {
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .areTherePriorYearIssues(Boolean.TRUE)
                .priorYearIssues(Set.of(
                        VerifierComment.builder()
                                .reference("")
                                .explanation("")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(2, violations.size());
    }

    @Test
    void when_are_there_prior_year_issues_true_then_null_prior_year_issues_invalid() {
        Set<VerifierComment> priorYearIssuesSet = new LinkedHashSet<>();
        priorYearIssuesSet.add(null);
        final UncorrectedNonConformities uncorrectedNonConformities = UncorrectedNonConformities.builder()
                .areThereUncorrectedNonConformities(Boolean.FALSE)
                .areTherePriorYearIssues(Boolean.TRUE)
                .priorYearIssues(priorYearIssuesSet)
                .build();

        final Set<ConstraintViolation<UncorrectedNonConformities>> violations = validator.validate(uncorrectedNonConformities);

        assertEquals(1, violations.size());
    }
}
