package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUncorrectedNonConformities;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationReportUncorrectedNonConformitiesValidatorTest {

    private final AviationAerUkEtsVerificationReportUncorrectedNonConformitiesValidator validator =
        new AviationAerUkEtsVerificationReportUncorrectedNonConformitiesValidator() ;

    @Test
    void getPrefix() {
        assertEquals("B", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.TRUE)
                    .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder().reference("B1").explanation("Explanation 1").materialEffect(Boolean.TRUE).build(),
                        UncorrectedItem.builder().reference("B2").explanation("Explanation 2").materialEffect(Boolean.FALSE).build()
                    ))
                    .existPriorYearIssues(Boolean.FALSE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_non_conformities_exist_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.FALSE)
                    .existPriorYearIssues(Boolean.TRUE)
                    .priorYearIssues(Set.of(VerifierComment.builder().reference("E1").explanation("explanation").build()))
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
                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.TRUE)
                    .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder().reference("B1").explanation("Explanation 1").materialEffect(Boolean.TRUE).build(),
                        UncorrectedItem.builder().reference("D18").explanation("Explanation 182").materialEffect(Boolean.FALSE).build()
                    ))
                    .existPriorYearIssues(Boolean.FALSE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}