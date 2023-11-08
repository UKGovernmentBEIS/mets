package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorHelper;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class AviationAerCorsiaValidatorService implements
    AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> {

    private final List<AviationAerCorsiaContextValidator> aviationAerCorsiaContextValidators;
    private final AviationAerCorsiaVerificationReportValidatorService corsiaVerificationReportValidatorService;

    @Override
    public void validate(final @Valid @NotNull AviationAerCorsiaContainer aerContainer) {
        
        //validate aer
        this.validateAer(aerContainer);

        //validate verification report existence
        this.validateVerificationReportExistence(aerContainer);


        //validate verification report
        if (!ObjectUtils.isEmpty(aerContainer.getVerificationReport())) {
            corsiaVerificationReportValidatorService.validate(aerContainer.getVerificationReport(), aerContainer.getAer());
        }
    }

    @Override
    public void validateAer(final @Valid @NotNull AviationAerCorsiaContainer aerContainer) {
        
        final List<AviationAerValidationResult> aerValidationResults = new ArrayList<>();
        if (Boolean.TRUE.equals(aerContainer.getReportingRequired())) {
            aviationAerCorsiaContextValidators.forEach(v -> aerValidationResults.add(v.validate(aerContainer)));
        }
        final boolean isValid = aerValidationResults.stream().allMatch(AviationAerValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_AVIATION_AER, AviationAerValidatorHelper.extractAviationAerViolations(aerValidationResults));
        }
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }

    private void validateVerificationReportExistence(AviationAerCorsiaContainer aerContainer) {
        // todo to be implemented
    }
}
