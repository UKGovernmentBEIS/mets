package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerUncorrectedMisstatements;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaVerificationReportUncorrectedMisstatementsValidatorTest {

    private final AviationAerCorsiaVerificationReportUncorrectedMisstatementsValidator validator =
        new AviationAerCorsiaVerificationReportUncorrectedMisstatementsValidator();

    @Test
    void getPrefix() {
        assertEquals("A", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                    .exist(Boolean.TRUE)
                    .uncorrectedMisstatements(Set.of(
                        new UncorrectedItem("A1", "Explanation 1", Boolean.TRUE),
                        new UncorrectedItem("A1001", "Explanation 2", Boolean.FALSE)
                    ))
                    .build())
                .build())
            .build();
        AviationAerCorsia aerCorsia = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_misstaments_exist_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                    .exist(Boolean.FALSE)
                    .build())
                .build())
            .build();
        AviationAerCorsia aerCorsia = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                    .exist(Boolean.TRUE)
                    .uncorrectedMisstatements(Set.of(
                        new UncorrectedItem("A1", "Explanation 1", Boolean.TRUE),
                        new UncorrectedItem("Z100", "Explanation 2", Boolean.FALSE)
                    ))
                    .build())
                .build())
            .build();
        AviationAerCorsia aerCorsia = AviationAerCorsia.builder().build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, aerCorsia);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}