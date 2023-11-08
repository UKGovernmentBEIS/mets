package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerRecommendedImprovements;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaVerificationReportRecommendedImprovementsValidatorTest {

    private final AviationAerCorsiaVerificationReportRecommendedImprovementsValidator validator =
        new AviationAerCorsiaVerificationReportRecommendedImprovementsValidator() ;

    @Test
    void getPrefix() {
        assertEquals("D", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.TRUE)
                    .recommendedImprovements(Set.of(
                        VerifierComment.builder().reference("D1").explanation("explanation 1").build(),
                        VerifierComment.builder().reference("D10").explanation("explanation 10").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerCorsia aer = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aer);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_recommended_improvements_exist_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.FALSE)
                    .build())
                .build())
            .build();

        AviationAerCorsia aer = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aer);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                    .exist(Boolean.TRUE)
                    .recommendedImprovements(Set.of(
                        VerifierComment.builder().reference("D1").explanation("explanation 1").build(),
                        VerifierComment.builder().reference("A1").explanation("explanation 1").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerCorsia aer = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aer);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}