package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorHelper;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;

import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class AviationAerCorsiaVerificationReportValidatorService {

    private final List<AviationAerCorsiaVerificationReportContextValidator> aviationAerCorsiaVerificationReportContextValidators;

    public void validate(final @Valid @NotNull AviationAerCorsiaVerificationReport verificationReport, AviationAerCorsia aer) {
        final List<AviationAerValidationResult> verificationReportValidationResults = new ArrayList<>();

        aviationAerCorsiaVerificationReportContextValidators
            .forEach(v -> verificationReportValidationResults.add(v.validate(verificationReport, aer)));

        final boolean isValid = verificationReportValidationResults.stream().allMatch(AviationAerValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT,
                AviationAerValidatorHelper.extractAviationAerViolations(verificationReportValidationResults));
        }
    }
}
