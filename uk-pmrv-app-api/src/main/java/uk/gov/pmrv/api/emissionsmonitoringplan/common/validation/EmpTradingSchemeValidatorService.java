package uk.gov.pmrv.api.emissionsmonitoringplan.common.validation;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

import jakarta.validation.Valid;

public interface EmpTradingSchemeValidatorService<T extends EmissionsMonitoringPlanContainer> {

    void validateEmissionsMonitoringPlan(@Valid T empContainer);

    EmissionTradingScheme getEmissionTradingScheme();
}
