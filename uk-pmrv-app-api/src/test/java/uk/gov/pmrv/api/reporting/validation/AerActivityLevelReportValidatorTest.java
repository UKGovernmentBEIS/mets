package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.ActivityLevelReport;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AerActivityLevelReportValidatorTest {

    private final AerActivityLevelReportValidator aerActivityLevelReportValidator =
        new AerActivityLevelReportValidator();

    @Test
    @DisplayName("invalid when ghge and activity level null")
    void invalid_when_ghge_and_activity_level_null() {
        AerContainer aerContainer = AerContainer.builder()
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .build())
            .aer(Aer.builder()
                .activityLevelReport(null)
                .build())
            .build();

        AerValidationResult aerValidationResult = aerActivityLevelReportValidator.validate(aerContainer);
        assertFalse(aerValidationResult.isValid());
        assertEquals(1, aerValidationResult.getAerViolations().size());
        assertEquals(AerViolation.AerViolationMessage.ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE.getMessage(),
            aerValidationResult.getAerViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("invalid when hse and activity level not null")
    void invalid_when_hse_and_activity_level_not_null() {
        AerContainer aerContainer = AerContainer.builder()
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.HSE)
                .build())
            .aer(Aer.builder()
                .activityLevelReport(ActivityLevelReport.builder()
                    .freeAllocationOfAllowances(false)
                    .build())
                .build())
            .build();

        AerValidationResult aerValidationResult = aerActivityLevelReportValidator.validate(aerContainer);
        assertFalse(aerValidationResult.isValid());
        assertEquals(1, aerValidationResult.getAerViolations().size());
        assertEquals(AerViolation.AerViolationMessage.ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE.getMessage(),
            aerValidationResult.getAerViolations().get(0).getMessage());
    }

    @Test
    @DisplayName("valid when ghge and activity level not null")
    void valid_when_ghge_and_activity_level_not_null() {
        AerContainer aerContainer = AerContainer.builder()
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .build())
            .aer(Aer.builder()
                .activityLevelReport(ActivityLevelReport.builder()
                    .freeAllocationOfAllowances(false)
                    .build())
                .build())
            .build();

        AerValidationResult aerValidationResult = aerActivityLevelReportValidator.validate(aerContainer);
        assertTrue(aerValidationResult.isValid());
        assertEquals(0, aerValidationResult.getAerViolations().size());
    }
}