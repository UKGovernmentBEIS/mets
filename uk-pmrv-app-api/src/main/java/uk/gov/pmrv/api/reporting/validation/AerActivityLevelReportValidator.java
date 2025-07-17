package uk.gov.pmrv.api.reporting.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AerActivityLevelReportValidator implements AerContextValidator {

    @Override
    public AerValidationResult validate(AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();
        //TODO: waste
        if (aerContainer.getPermitOriginatedData().getPermitType() == PermitType.GHGE && aerContainer.getAer().getActivityLevelReport() == null) {
            violations.add(new AerViolation(AerContainer.class.getSimpleName(),
                AerViolation.AerViolationMessage.ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE));
        } else if (aerContainer.getPermitOriginatedData().getPermitType() == PermitType.HSE && aerContainer.getAer().getActivityLevelReport() != null) {
            violations.add(new AerViolation(AerContainer.class.getSimpleName(),
                AerViolation.AerViolationMessage.ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE));
        }
        return AerValidationResult.builder()
            .valid(violations.isEmpty())
            .aerViolations(violations)
            .build();
    }
}
