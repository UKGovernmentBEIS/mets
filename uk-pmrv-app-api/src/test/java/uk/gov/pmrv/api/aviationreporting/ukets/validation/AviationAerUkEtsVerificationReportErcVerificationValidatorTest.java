package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationReportErcVerificationValidatorTest {

    private final AviationAerUkEtsVerificationReportErcVerificationValidator validator =
        new AviationAerUkEtsVerificationReportErcVerificationValidator() ;

    @Test
    void validate_valid_when_saf_exists() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(Boolean.TRUE)
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
                    .safBatchClaimsReviewed(Boolean.TRUE)
                    .reviewResults("review results")
                    .noDoubleCountingConfirmation("no double counting")
                    .evidenceAllCriteriaMetExist(Boolean.TRUE)
                    .complianceWithUkEtsRequirementsExist(Boolean.TRUE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_valid_when_saf_not_exists() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(Boolean.FALSE)
            .verificationData(AviationAerUkEtsVerificationData.builder().build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid_when_saf_exists() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(Boolean.TRUE)
            .verificationData(AviationAerUkEtsVerificationData.builder().build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid_when_saf_not_exists() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(Boolean.FALSE)
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
                    .safBatchClaimsReviewed(Boolean.TRUE)
                    .reviewResults("review results")
                    .noDoubleCountingConfirmation("no double counting")
                    .evidenceAllCriteriaMetExist(Boolean.TRUE)
                    .complianceWithUkEtsRequirementsExist(Boolean.TRUE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }

    @Test
    void validate() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
                    .safBatchClaimsReviewed(Boolean.TRUE)
                    .reviewResults("review results")
                    .noDoubleCountingConfirmation("no double counting")
                    .evidenceAllCriteriaMetExist(Boolean.TRUE)
                    .complianceWithUkEtsRequirementsExist(Boolean.TRUE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
    }
}