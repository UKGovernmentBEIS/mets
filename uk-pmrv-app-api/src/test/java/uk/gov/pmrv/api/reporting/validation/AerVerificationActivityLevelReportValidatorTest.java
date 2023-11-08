package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.ActivityLevelReport;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

public class AerVerificationActivityLevelReportValidatorTest {

    private AerVerificationActivityLevelReportValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AerVerificationActivityLevelReportValidator();
    }

    @Test
    void testValidateWhenActivityLevelReportIsNotApplicableForHSE() {
        AerVerificationReport verificationReport = new AerVerificationReport();
        verificationReport.setVerificationData(new AerVerificationData());
        verificationReport.getVerificationData().setActivityLevelReport(new ActivityLevelReport());

        PermitOriginatedData permitOriginatedData = new PermitOriginatedData();
        permitOriginatedData.setPermitType(PermitType.HSE);

        AerValidationResult result = validator.validate(verificationReport, permitOriginatedData);

        Assertions.assertFalse(result.isValid());
        Assertions.assertEquals(1, result.getAerViolations().size());
        Assertions.assertEquals(AerViolation.AerViolationMessage.VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE.getMessage(),
            result.getAerViolations().get(0).getMessage());
    }

    @Test
    void testValidateWhenActivityLevelReportIsNotApplicableForGHGE() {
        AerVerificationReport verificationReport = new AerVerificationReport();
        verificationReport.setVerificationData(new AerVerificationData());

        PermitOriginatedData permitOriginatedData = new PermitOriginatedData();
        permitOriginatedData.setPermitType(PermitType.GHGE);

        AerValidationResult result = validator.validate(verificationReport, permitOriginatedData);

        Assertions.assertFalse(result.isValid());
        Assertions.assertEquals(1, result.getAerViolations().size());
        Assertions.assertEquals(AerViolation.AerViolationMessage.VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE.getMessage(),
            result.getAerViolations().get(0).getMessage());
    }

    @Test
    void testValidateWhenActivityLevelReportIsApplicableForHSE() {
        AerVerificationReport verificationReport = new AerVerificationReport();
        verificationReport.setVerificationData(new AerVerificationData());

        PermitOriginatedData permitOriginatedData = new PermitOriginatedData();
        permitOriginatedData.setPermitType(PermitType.HSE);

        AerValidationResult result = validator.validate(verificationReport, permitOriginatedData);

        Assertions.assertTrue(result.isValid());
        Assertions.assertEquals(0, result.getAerViolations().size());
    }

    @Test
    void testValidateWhenActivityLevelReportIsApplicableForGHGE() {
        AerVerificationReport verificationReport = new AerVerificationReport();
        verificationReport.setVerificationData(new AerVerificationData());
        verificationReport.getVerificationData().setActivityLevelReport(new ActivityLevelReport());

        PermitOriginatedData permitOriginatedData = new PermitOriginatedData();
        permitOriginatedData.setPermitType(PermitType.GHGE);

        AerValidationResult result = validator.validate(verificationReport, permitOriginatedData);

        Assertions.assertTrue(result.isValid());
        Assertions.assertEquals(0, result.getAerViolations().size());
    }
}
