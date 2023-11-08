package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerifierDetailsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_team_leader_null_invalid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.FALSE)
                        .impartialityAssessmentResult("impartiality assessment")
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(1, violations.size());
    }

    @Test
    void when_team_leader_fields_empty_invalid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.FALSE)
                        .impartialityAssessmentResult("impartiality assessment")
                        .build())
                .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder().build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(4, violations.size());
    }

    @Test
    void when_six_verifications_conducted_true_invalid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.TRUE)
                        .impartialityAssessmentResult("impartiality assessment")
                        .build())
                .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .role("role")
                        .position("position")
                        .email("dk@gmail.com")
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.impartialityAssessmentResult}",
                        "{aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.breakTaken}");
    }

    @Test
    void when_brake_taken_false_invalid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.TRUE)
                        .breakTaken(Boolean.FALSE)
                        .build())
                .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .role("role")
                        .position("position")
                        .email("dk@gmail.com")
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("{aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.reason}");
    }

    @Test
    void when_six_verifications_conducted_false_assessment_null_invalid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.FALSE)
                        .build())
                .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .role("role")
                        .position("position")
                        .email("dk@gmail.com")
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("{aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.impartialityAssessmentResult}");
    }

    @Test
    void validate_valid() {
        final AviationAerCorsiaVerifierDetails verifierDetails = AviationAerCorsiaVerifierDetails.builder()
                .interestConflictAvoidance(AviationAerCorsiaInterestConflictAvoidance.builder()
                        .sixVerificationsConducted(Boolean.TRUE)
                        .breakTaken(Boolean.FALSE)
                        .reason("reason")
                        .build())
                .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .role("role")
                        .position("position")
                        .email("dk@gmail.com")
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaVerifierDetails>> violations = validator.validate(verifierDetails);

        assertEquals(0, violations.size());
    }
}
