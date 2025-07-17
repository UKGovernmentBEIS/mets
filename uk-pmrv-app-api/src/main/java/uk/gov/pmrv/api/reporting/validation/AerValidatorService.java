package uk.gov.pmrv.api.reporting.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class AerValidatorService {

    private final List<AerContextValidator> aerContextValidators;
    private final AerVerificationReportExistenceValidator aerVerificationReportExistenceValidator;
    private final List<AerVerificationReportContextValidator> aerVerificationReportContextValidators;

    public void validate(@Valid @NotNull AerContainer aerContainer, Long accountId) {
        // Validate AER data
        validateAer(aerContainer);

        // Validate if Verification report is needed
        aerVerificationReportExistenceValidator.validate(aerContainer, accountId);

        // Validate Verification report data
        if (!ObjectUtils.isEmpty(aerContainer.getVerificationReport())) {
            validateVerificationReport(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());
        }
    }

    public void validateAer(@Valid @NotNull AerContainer aerContainer) {
        List<AerValidationResult> aerValidationResults = new ArrayList<>();
        aerContextValidators.forEach(v -> aerValidationResults.add(v.validate(aerContainer)));

        boolean isValid = aerValidationResults.stream().allMatch(AerValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(MetsErrorCode.INVALID_AER,
                AerValidatorHelper.extractAerViolations(aerValidationResults));
        }
    }

    public void validateVerificationReport(@Valid @NotNull AerVerificationReport verificationReport,
                                           @Valid @NotNull PermitOriginatedData permitOriginatedData) {
        List<AerValidationResult> verificationReportValidationResults = new ArrayList<>();
        aerVerificationReportContextValidators.forEach(v -> verificationReportValidationResults
            .add(v.validate(verificationReport, permitOriginatedData)));

        boolean isValid = verificationReportValidationResults.stream().allMatch(AerValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(MetsErrorCode.INVALID_AER_VERIFICATION_REPORT,
                AerValidatorHelper.extractAerViolations(verificationReportValidationResults));
        }
    }
}
