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
class UncorrectedMisstatementsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_are_there_uncorrected_misstatements_false_then_valid() {
        final UncorrectedMisstatements uncorrectedMisstatements = UncorrectedMisstatements.builder()
                .areThereUncorrectedMisstatements(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<UncorrectedMisstatements>> violations = validator.validate(uncorrectedMisstatements);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_misstatements_true_then_valid() {
        final UncorrectedMisstatements uncorrectedMisstatements = UncorrectedMisstatements.builder()
                .areThereUncorrectedMisstatements(Boolean.TRUE)
                .uncorrectedMisstatements(Set.of(
                        UncorrectedItem.builder()
                                .reference("A1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedMisstatements>> violations = validator.validate(uncorrectedMisstatements);

        assertEquals(0, violations.size());
    }

    @Test
    void when_are_there_uncorrected_misstatements_false_then_invalid() {
        final UncorrectedMisstatements uncorrectedMisstatements = UncorrectedMisstatements.builder()
                .areThereUncorrectedMisstatements(Boolean.FALSE)
                .uncorrectedMisstatements(Set.of(
                        UncorrectedItem.builder()
                                .reference("A1")
                                .explanation("explanation")
                                .materialEffect(Boolean.TRUE)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedMisstatements>> violations = validator.validate(uncorrectedMisstatements);

        assertEquals(1, violations.size());
    }

    @Test
    void when_are_there_uncorrected_misstatements_true_then_invalid() {
        final UncorrectedMisstatements uncorrectedMisstatements = UncorrectedMisstatements.builder()
                .areThereUncorrectedMisstatements(Boolean.TRUE)
                .uncorrectedMisstatements(Set.of(
                        UncorrectedItem.builder()
                                .reference("")
                                .explanation("")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<UncorrectedMisstatements>> violations = validator.validate(uncorrectedMisstatements);

        assertEquals(3, violations.size());
    }

    @Test
    void when_are_there_uncorrected_misstatements_true_then_null_uncorrected_misstatement_invalid() {
        Set<UncorrectedItem> uncorrectedMisstatementsSet = new LinkedHashSet<>();
        uncorrectedMisstatementsSet.add(null);
        final UncorrectedMisstatements uncorrectedMisstatements = UncorrectedMisstatements.builder()
                .areThereUncorrectedMisstatements(Boolean.TRUE)
                .uncorrectedMisstatements(uncorrectedMisstatementsSet)
                .build();

        final Set<ConstraintViolation<UncorrectedMisstatements>> violations = validator.validate(uncorrectedMisstatements);

        assertEquals(1, violations.size());
    }
}
