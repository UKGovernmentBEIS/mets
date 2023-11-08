package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerNotVerifiedDecisionReason;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerNotVerifiedDecisionReasonType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AviationAerNotVerifiedDecisionReasonTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_uncorrected_material_misstatement_without_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.UNCORRECTED_MATERIAL_MISSTATEMENT)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_uncorrected_material_misstatement_with_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.UNCORRECTED_MATERIAL_MISSTATEMENT)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_uncorrected_material_non_conformity_without_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.UNCORRECTED_MATERIAL_NON_CONFORMITY)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_uncorrected_material_non_conformity_with_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.UNCORRECTED_MATERIAL_NON_CONFORMITY)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_verification_data_or_information_limitations_with_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_verification_data_or_information_limitations_without_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_scope_limitations_due_to_lack_of_clarity_with_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_scope_limitations_due_to_lack_of_clarity_without_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_scope_limitations_of_approved_monitoring_plan_with_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_scope_limitations_of_approved_monitoring_plan_without_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_not_approved_monitoring_plan_by_regulator_with_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_not_approved_monitoring_plan_by_regulator_without_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

    @Test
    void when_another_reason_with_details_valid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.ANOTHER_REASON)
            .details("details")
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(0, violations.size());
    }

    @Test
    void when_another_reason_without_details_invalid() {
        AviationAerNotVerifiedDecisionReason notVerifiedDecisionReason = AviationAerNotVerifiedDecisionReason.builder()
            .type(AviationAerNotVerifiedDecisionReasonType.ANOTHER_REASON)
            .build();

        Set<ConstraintViolation<AviationAerNotVerifiedDecisionReason>> violations = validator.validate(notVerifiedDecisionReason);

        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{aviationAerVerificationData.decision.notVerifiedDecisionReason.details}");
    }

}