package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUncorrectedNonConformities;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerUkEtsVerificationReportPriorYearIssuesValidatorTest {

    private final AviationAerUkEtsVerificationReportPriorYearIssuesValidator validator =
        new AviationAerUkEtsVerificationReportPriorYearIssuesValidator() ;

    @Test
    void getPrefix() {
        assertEquals("E", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.FALSE)
                    .existPriorYearIssues(Boolean.TRUE)
                    .priorYearIssues(Set.of(
                        VerifierComment.builder().reference("E1").explanation("Explanation 1").build(),
                        VerifierComment.builder().reference("E345").explanation("Explanation 345").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_prior_year_issues_exist_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.FALSE)
                    .existPriorYearIssues(Boolean.FALSE)
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
                    .existUncorrectedNonConformities(Boolean.FALSE)
                    .existPriorYearIssues(Boolean.TRUE)
                    .priorYearIssues(Set.of(
                        VerifierComment.builder().reference("E1").explanation("Explanation 1").build(),
                        VerifierComment.builder().reference("Z345").explanation("Explanation 345").build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport);
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}