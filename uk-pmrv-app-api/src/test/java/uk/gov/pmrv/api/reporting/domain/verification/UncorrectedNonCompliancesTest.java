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

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UncorrectedNonCompliancesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_are_there_uncorrected_non_compliances_false_then_valid() {
        final UncorrectedNonCompliances uncorrectedNonCompliances = UncorrectedNonCompliances.builder()
                .areThereUncorrectedNonCompliances(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedNonCompliances>> violations = validator.validate(uncorrectedNonCompliances);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_compliances_true_then_valid() {
        final UncorrectedNonCompliances uncorrectedNonCompliances = UncorrectedNonCompliances.builder()
                .areThereUncorrectedNonCompliances(Boolean.TRUE)
                .uncorrectedNonCompliances(Set.of(
                        UncorrectedItem.builder()
                                .reference("D1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonCompliances>> violations = validator.validate(uncorrectedNonCompliances);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_compliances_false_then_invalid() {
        final UncorrectedNonCompliances uncorrectedNonCompliances = UncorrectedNonCompliances.builder()
                .areThereUncorrectedNonCompliances(Boolean.FALSE)
                .uncorrectedNonCompliances(Set.of(
                        UncorrectedItem.builder()
                                .reference("D1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonCompliances>> violations = validator.validate(uncorrectedNonCompliances);

        assertEquals(1, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_compliances_true_then_invalid() {
        final UncorrectedNonCompliances uncorrectedNonCompliances = UncorrectedNonCompliances.builder()
                .areThereUncorrectedNonCompliances(Boolean.TRUE)
                .uncorrectedNonCompliances(Set.of(
                        UncorrectedItem.builder()
                                .reference("")
                                .explanation("")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedNonCompliances>> violations = validator.validate(uncorrectedNonCompliances);

        assertEquals(3, violations.size());
    }

    @Test
    void when_are_there_uncorrected_non_compliances_true_then_null_uncorrected_non_compliance_invalid() {
        Set<UncorrectedItem> uncorrectedNonCompliancesSet = new LinkedHashSet<>();
        uncorrectedNonCompliancesSet.add(null);
        final UncorrectedNonCompliances uncorrectedNonCompliances = UncorrectedNonCompliances.builder()
                .areThereUncorrectedNonCompliances(Boolean.TRUE)
                .uncorrectedNonCompliances(uncorrectedNonCompliancesSet)
                .build();

        final Set<ConstraintViolation<UncorrectedNonCompliances>> violations = validator.validate(uncorrectedNonCompliances);

        assertEquals(1, violations.size());
    }
}
