package uk.gov.pmrv.api.reporting.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AerVerificationActivityLevelReportValidator implements AerVerificationReportContextValidator {

    @Override
    public AerValidationResult validate(AerVerificationReport verificationReport,
                                        PermitOriginatedData permitOriginatedData) {
        List<AerViolation> violations = new ArrayList<>();
        //TODO: waste
        if (isActivityLevelReportNotApplicableForHSE(verificationReport, permitOriginatedData)) {
            violations.add(new AerViolation(AerVerificationReport.class.getSimpleName(),
                AerViolation.AerViolationMessage.VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE));
        }
        if (isActivityLevelReportNotApplicableForGHGE(verificationReport, permitOriginatedData)) {
            violations.add(new AerViolation(AerVerificationReport.class.getSimpleName(),
                AerViolation.AerViolationMessage.VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE));
        }
        return AerValidationResult.builder()
            .valid(violations.isEmpty())
            .aerViolations(violations)
            .build();
    }

    private boolean isActivityLevelReportNotApplicableForHSE(AerVerificationReport verificationReport,
                                                             PermitOriginatedData permitOriginatedData) {
        return permitOriginatedData.getPermitType() == PermitType.HSE
            && verificationReport.getVerificationData().getActivityLevelReport() != null;
    }

    private boolean isActivityLevelReportNotApplicableForGHGE(AerVerificationReport verificationReport,
                                                              PermitOriginatedData permitOriginatedData) {
        return permitOriginatedData.getPermitType() == PermitType.GHGE
            && verificationReport.getVerificationData().getActivityLevelReport() == null;
    }
}

