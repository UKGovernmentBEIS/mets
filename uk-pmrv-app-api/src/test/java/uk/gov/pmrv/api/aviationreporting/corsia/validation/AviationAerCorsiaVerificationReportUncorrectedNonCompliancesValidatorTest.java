package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerUncorrectedNonCompliances;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaVerificationReportUncorrectedNonCompliancesValidatorTest {

    private final AviationAerCorsiaVerificationReportUncorrectedNonCompliancesValidator validator =
            new AviationAerCorsiaVerificationReportUncorrectedNonCompliancesValidator();


    @Test
    void getPrefix() {
        assertEquals("C", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
                                .exist(Boolean.TRUE)
                                .uncorrectedNonCompliances(Set.of(
                                        new UncorrectedItem("C1", "Explanation 1", Boolean.TRUE),
                                        new UncorrectedItem("C201", "Explanation 2", Boolean.FALSE)
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
    void validate_when_no_non_compliances_exist_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
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
                        .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
                                .exist(Boolean.TRUE)
                                .uncorrectedNonCompliances(Set.of(
                                        new UncorrectedItem("D1", "Explanation 1", Boolean.TRUE),
                                        new UncorrectedItem("D18", "Explanation 2", Boolean.FALSE)
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
