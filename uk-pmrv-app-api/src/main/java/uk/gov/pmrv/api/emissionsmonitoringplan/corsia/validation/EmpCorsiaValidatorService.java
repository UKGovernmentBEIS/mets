package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;

@Service
@Validated
@RequiredArgsConstructor
public class EmpCorsiaValidatorService implements EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> {

    private final List<EmpCorsiaContextValidator> empCorsiaContextValidators;

    @Override
    public void validateEmissionsMonitoringPlan(@Valid EmissionsMonitoringPlanCorsiaContainer empContainer) {
        List<EmissionsMonitoringPlanValidationResult> empValidationResults = new ArrayList<>();
        empCorsiaContextValidators.forEach(v -> empValidationResults.add(v.validate(empContainer)));

        boolean isValid = empValidationResults.stream().allMatch(EmissionsMonitoringPlanValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.INVALID_EMP, extractEmissionsMonitoringPlanViolations(empValidationResults));
        }
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }

    private Object[] extractEmissionsMonitoringPlanViolations(List<EmissionsMonitoringPlanValidationResult> empValidationResults) {
        return empValidationResults.stream()
            .filter(empValidationResult -> !empValidationResult.isValid())
            .flatMap(empValidationResult -> empValidationResult.getEmpViolations().stream())
            .toArray();
    }
}
