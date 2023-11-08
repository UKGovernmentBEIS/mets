package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorHelper;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class AviationAerUkEtsVerificationReportValidatorService {

    private final List<AviationAerUkEtsVerificationReportContextValidator> aviationAerUkEtsVerificationReportContextValidators;

    public void validate(@Valid @NotNull AviationAerUkEtsVerificationReport verificationReport) {
        List<AviationAerValidationResult> verificationReportValidationResults = new ArrayList<>();
        aviationAerUkEtsVerificationReportContextValidators
            .forEach(v -> verificationReportValidationResults.add(v.validate(verificationReport)));

        boolean isValid = verificationReportValidationResults.stream().allMatch(AviationAerValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT,
                AviationAerValidatorHelper.extractAviationAerViolations(verificationReportValidationResults));
        }
    }
}
