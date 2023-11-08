package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerUncorrectedMisstatements;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationReportUncorrectedMisstatementsValidatorTest {

    private final AviationAerUkEtsVerificationReportUncorrectedMisstatementsValidator validator =
        new AviationAerUkEtsVerificationReportUncorrectedMisstatementsValidator() ;

    @Test
    void getPrefix() {
        assertEquals("A", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                    .exist(Boolean.TRUE)
                    .uncorrectedMisstatements(Set.of(
                        new UncorrectedItem("A1", "Explanation 1", Boolean.TRUE),
                        new UncorrectedItem("A1001", "Explanation 2", Boolean.FALSE)
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_misstaments_exist_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
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
                .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                    .exist(Boolean.TRUE)
                    .uncorrectedMisstatements(Set.of(
                        new UncorrectedItem("A1", "Explanation 1", Boolean.TRUE),
                        new UncorrectedItem("Z100", "Explanation 2", Boolean.FALSE)
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}