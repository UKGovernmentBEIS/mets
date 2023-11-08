package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaUncorrectedNonConformities;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaVerificationReportUncorrectedNonConformitiesValidatorTest {

    private final AviationAerCorsiaVerificationReportUncorrectedNonConformitiesValidator validator =
        new AviationAerCorsiaVerificationReportUncorrectedNonConformitiesValidator();

    @Test
    void getPrefix() {
        assertEquals("B", validator.getPrefix());
    }

    @Test
    void validate_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedNonConformities(AviationAerCorsiaUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.TRUE)
                    .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder().reference("B1").explanation("Explanation 1").materialEffect(Boolean.TRUE).build(),
                        UncorrectedItem.builder().reference("B2").explanation("Explanation 2").materialEffect(Boolean.FALSE).build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport,
            AviationAerCorsia.builder().build());
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_when_no_non_conformities_exist_valid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedNonConformities(AviationAerCorsiaUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.FALSE)
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, AviationAerCorsia.builder().build());
        assertTrue(validationResult.isValid());
        assertEquals(0, validationResult.getAerViolations().size());
    }

    @Test
    void validate_invalid() {
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .uncorrectedNonConformities(AviationAerCorsiaUncorrectedNonConformities.builder()
                    .existUncorrectedNonConformities(Boolean.TRUE)
                    .uncorrectedNonConformities(Set.of(
                        UncorrectedItem.builder().reference("B1").explanation("Explanation 1").materialEffect(Boolean.TRUE).build(),
                        UncorrectedItem.builder().reference("D18").explanation("Explanation 182").materialEffect(Boolean.FALSE).build()
                    ))
                    .build())
                .build())
            .build();

        AviationAerValidationResult validationResult = validator.validate(verificationReport, AviationAerCorsia.builder().build());
        assertFalse(validationResult.isValid());
        assertEquals(1, validationResult.getAerViolations().size());
    }
}