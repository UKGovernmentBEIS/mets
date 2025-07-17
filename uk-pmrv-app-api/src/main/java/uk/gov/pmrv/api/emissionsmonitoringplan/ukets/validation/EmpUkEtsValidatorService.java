package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class EmpUkEtsValidatorService implements EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> {

    private final List<EmpUkEtsContextValidator> empUkEtsContextValidators;

    @Override
    public void validateEmissionsMonitoringPlan(@Valid EmissionsMonitoringPlanUkEtsContainer empContainer) {
        List<EmissionsMonitoringPlanValidationResult> empValidationResults = new ArrayList<>();
        empUkEtsContextValidators.forEach(v -> empValidationResults.add(v.validate(empContainer)));

        boolean isValid = empValidationResults.stream().allMatch(EmissionsMonitoringPlanValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.INVALID_EMP, extractEmissionsMonitoringPlanViolations(empValidationResults));
        }
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }

    private Object[] extractEmissionsMonitoringPlanViolations(List<EmissionsMonitoringPlanValidationResult> empValidationResults) {
        return empValidationResults.stream()
            .filter(empValidationResult -> !empValidationResult.isValid())
            .flatMap(empValidationResult -> empValidationResult.getEmpViolations().stream())
            .toArray();
    }
}
