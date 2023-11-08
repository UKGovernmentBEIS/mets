package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedNonConformities;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AerVerificationReportPriorYearIssuesValidatorTest {

    @InjectMocks
    private AerVerificationReportPriorYearIssuesValidator aerVerificationReportPriorYearIssuesValidator;

    @Test
    void validate() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                                        .areTherePriorYearIssues(Boolean.TRUE)
                                        .priorYearIssues(Set.of(
                                                new VerifierComment("E1", "Explanation 1"),
                                                new VerifierComment("E12", "Explanation 2")
                                        ))
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportPriorYearIssuesValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertTrue(aerValidationResult.isValid());
        assertEquals(0, aerValidationResult.getAerViolations().size());
    }

    @Test
    void validate_empty_set_valid() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                                        .areTherePriorYearIssues(Boolean.FALSE)
                                        .priorYearIssues(Set.of())
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportPriorYearIssuesValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertTrue(aerValidationResult.isValid());
        assertEquals(0, aerValidationResult.getAerViolations().size());
    }

    @Test
    void validate_not_valid() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                                        .areTherePriorYearIssues(Boolean.TRUE)
                                        .priorYearIssues(Set.of(
                                                new VerifierComment("B1", "Explanation 1"),
                                                new VerifierComment("X12", "Explanation 2")
                                        ))
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportPriorYearIssuesValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertFalse(aerValidationResult.isValid());
        assertEquals(1, aerValidationResult.getAerViolations().size());
        assertEquals(AerViolation.AerViolationMessage.VERIFICATION_INVALID_PRIOR_YEAR_ISSUE_REFERENCE.getMessage(), aerValidationResult.getAerViolations().get(0).getMessage());

    }
}
