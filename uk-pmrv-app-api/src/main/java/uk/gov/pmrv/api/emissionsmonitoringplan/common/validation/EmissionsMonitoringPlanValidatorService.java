package uk.gov.pmrv.api.emissionsmonitoringplan.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmissionsMonitoringPlanValidatorService {

    private final AviationAccountQueryService accountQueryService;
    private final List<EmpTradingSchemeValidatorService<? extends EmissionsMonitoringPlanContainer>> empTradingSchemeValidatorServices;

    @SuppressWarnings("unchecked")
    public void validateEmissionsMonitoringPlan(Long accountId, EmissionsMonitoringPlanContainer empContainer) {
        EmissionTradingScheme emissionTradingScheme = accountQueryService.getAviationAccountInfoDTOById(accountId).getEmissionTradingScheme();
        getValidatorService(emissionTradingScheme).validateEmissionsMonitoringPlan(empContainer);
    }

    private EmpTradingSchemeValidatorService getValidatorService(EmissionTradingScheme emissionTradingScheme) {
        return empTradingSchemeValidatorServices.stream()
            .filter(empTradingSchemeValidatorService -> emissionTradingScheme.equals(empTradingSchemeValidatorService.getEmissionTradingScheme()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No suitable validator found"));
    }
}
