package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerificationReportErcVerificationValidatorTest {

    @InjectMocks
    private AviationAerCorsiaVerificationReportErcVerificationValidator validator;

    @Test
    void validate_when_no_exist_valid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
                .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                        .exist(false)
                        .build())
                .build();

        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder().build())
                .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);
        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_exist_valid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
                .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                        .exist(true)
                        .build())
                .build();

        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .emissionsReductionClaimVerification(AviationAerCorsiaEmissionsReductionClaimVerification.builder()
                                .reviewResults("Review results")
                                .build())
                        .build())
                .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);

        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_no_exist_invalid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
                .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                        .exist(false)
                        .build())
                .build();

        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .emissionsReductionClaimVerification(AviationAerCorsiaEmissionsReductionClaimVerification.builder()
                                .reviewResults("Review results")
                                .build())
                        .build())
                .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);

        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_exist_invalid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
                .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                        .exist(true)
                        .build())
                .build();

        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder().build())
                .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);

        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}
