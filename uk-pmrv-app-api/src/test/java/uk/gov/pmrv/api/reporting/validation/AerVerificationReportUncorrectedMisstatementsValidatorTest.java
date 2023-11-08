package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedMisstatements;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AerVerificationReportUncorrectedMisstatementsValidatorTest {

    @InjectMocks
    private AerVerificationReportUncorrectedMisstatementsValidator aerVerificationReportUncorrectedMisstatementsValidator;

    @Test
    void validate() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                                        .areThereUncorrectedMisstatements(Boolean.TRUE)
                                        .uncorrectedMisstatements(Set.of(
                                                new UncorrectedItem("A1", "Explanation 1", Boolean.TRUE),
                                                new UncorrectedItem("A12", "Explanation 2", Boolean.FALSE)
                                        ))
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportUncorrectedMisstatementsValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertTrue(aerValidationResult.isValid());
        assertEquals(0, aerValidationResult.getAerViolations().size());
    }

    @Test
    void validate_empty_set_valid() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                                        .areThereUncorrectedMisstatements(Boolean.FALSE)
                                        .uncorrectedMisstatements(Set.of())
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportUncorrectedMisstatementsValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertTrue(aerValidationResult.isValid());
        assertEquals(0, aerValidationResult.getAerViolations().size());
    }

    @Test
    void validate_not_valid() {

        final AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(
                        AerVerificationData.builder()
                                .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                                        .areThereUncorrectedMisstatements(Boolean.TRUE)
                                        .uncorrectedMisstatements(Set.of(
                                                new UncorrectedItem("A21", "Explanation 1", Boolean.TRUE),
                                                new UncorrectedItem("C12", "Explanation 2", Boolean.FALSE)
                                        ))
                                        .build())
                                .build())
                .build();

        final AerValidationResult aerValidationResult =
            aerVerificationReportUncorrectedMisstatementsValidator.validate(aerVerificationReport, new PermitOriginatedData());
        assertFalse(aerValidationResult.isValid());
        assertEquals(1, aerValidationResult.getAerViolations().size());
    }
}
