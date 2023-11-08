package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;

import jakarta.validation.Valid;

public interface EmpUkEtsContextValidator {

    EmissionsMonitoringPlanValidationResult validate(@Valid EmissionsMonitoringPlanUkEtsContainer empContainer);
}
