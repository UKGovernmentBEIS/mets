package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorHelper;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class AviationAerUkEtsValidatorService implements
    AviationAerTradingSchemeValidatorService<AviationAerUkEtsContainer> {

    private final List<AviationAerUkEtsContextValidator> aviationAerUkEtsContextValidators;
    private final AviationAerUkEtsVerificationReportValidatorService ukEtsVerificationReportValidatorService;

    @Override
    public void validate(@Valid @NotNull AviationAerUkEtsContainer aerContainer) {
        //validate aer
        validateAer(aerContainer);

        //validate verification report existence
        validateVerificationReportExistence(aerContainer);


        //validate verification report
        if (!ObjectUtils.isEmpty(aerContainer.getVerificationReport())) {
            ukEtsVerificationReportValidatorService.validate(aerContainer.getVerificationReport());
        }
    }

    @Override
    public void validateAer(@Valid @NotNull AviationAerUkEtsContainer aerContainer) {
        List<AviationAerValidationResult> aerValidationResults = new ArrayList<>();
        if (Boolean.TRUE.equals(aerContainer.getReportingRequired())) {
            aviationAerUkEtsContextValidators.forEach(v -> aerValidationResults.add(v.validate(aerContainer)));
        }
        boolean isValid = aerValidationResults.stream().allMatch(AviationAerValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_AVIATION_AER, AviationAerValidatorHelper.extractAviationAerViolations(aerValidationResults));
        }
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }

    private void validateVerificationReportExistence(AviationAerUkEtsContainer aerContainer) {
        AviationAerUkEts aviationAerUkEts = aerContainer.getAer();

        if(Boolean.TRUE.equals(aerContainer.getReportingRequired()) &&
            (aviationAerUkEts.getSaf().getExist() ||
                EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(aviationAerUkEts.getMonitoringApproach().getMonitoringApproachType()) ||
                EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS.equals(aviationAerUkEts.getMonitoringApproach().getMonitoringApproachType())
            ) && ObjectUtils.isEmpty(aerContainer.getVerificationReport())
        ) {
                throw new BusinessException(ErrorCode.INVALID_AVIATION_AER,
                    AviationAerValidationResult.builder().valid(false).aerViolations(List.of(
                        new AviationAerViolation(AviationAerUkEtsVerificationReport.class.getSimpleName(),
                            AviationAerViolation.AviationAerViolationMessage.NO_VERIFICATION_REPORT_FOUND))).build()
                );
        }
    }
}
