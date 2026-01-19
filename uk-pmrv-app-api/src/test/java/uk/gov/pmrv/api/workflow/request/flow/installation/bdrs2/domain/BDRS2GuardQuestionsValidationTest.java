package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BDRS2GuardQuestionsValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldFailValidation_whenWithdrawAndReasonIsNull() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW
                )
                .applicationWithdrawalReason(null)
                .covidAdjustments(Boolean.TRUE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.FALSE)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessageTemplate())
                .isEqualTo("{bdrs2.guardQuestions.applicationWithdrawalReason}");
    }

    @Test
    void shouldFailValidation_whenInEiteSectorIsTrueAndCbamIsNull() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT
                )
                .covidAdjustments(Boolean.TRUE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(null)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessageTemplate())
                .isEqualTo("{bdrs2.guardQuestions.requiresAdditionalSubInstallationSplitsForCbam}");
    }

    @Test
    void shouldPassValidation_whenWithdrawAndReasonIsNotNull() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW
                )
                .applicationWithdrawalReason("A very valid reason.")
                .covidAdjustments(Boolean.TRUE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.FALSE)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).isEmpty();
    }
    @Test
    void shouldPassValidation_whenInEiteSectorIsTrueAndCbamIsTrue() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT
                )
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.TRUE)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_whenInEiteSectorIsTrueAndCbamIsFalse() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT
                )
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.FALSE)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_whenInEiteSectorIsFalseAndCbamIsNull() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE
                )
                .covidAdjustments(Boolean.TRUE)
                .inEiteSector(Boolean.FALSE)
                .requiresAdditionalSubInstallationSplitsForCbam(null)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_whenAllFieldsPopulatedCorrectly() {
        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT
                )
                .covidAdjustments(Boolean.TRUE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.TRUE)
                .build();

        Set<ConstraintViolation<BDRS2GuardQuestions>> violations =
                validator.validate(guardQuestions);

        assertThat(violations).isEmpty();
    }
}
