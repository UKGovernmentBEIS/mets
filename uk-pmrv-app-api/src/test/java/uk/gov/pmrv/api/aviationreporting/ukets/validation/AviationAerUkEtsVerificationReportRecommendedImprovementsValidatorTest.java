package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerRecommendedImprovements;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationReportRecommendedImprovementsValidatorTest {

    private final AviationAerUkEtsVerificationReportRecommendedImprovementsValidator validator =
        new AviationAerUkEtsVerificationReportRecommendedImprovementsValidator() ;

    @Test
    void getPrefix() {
        assertEquals("D", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.TRUE)
                    .recommendedImprovements(Set.of(
                        VerifierComment.builder().reference("D1").explanation("explanation 1").build(),
                        VerifierComment.builder().reference("D10").explanation("explanation 10").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_recommended_improvements_exist_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.FALSE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.TRUE)
                    .recommendedImprovements(Set.of(
                        VerifierComment.builder().reference("D1").explanation("explanation 1").build(),
                        VerifierComment.builder().reference("A1").explanation("explanation 1").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}