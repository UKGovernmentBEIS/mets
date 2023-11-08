package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import jakarta.validation.Valid;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;

public interface EmpCorsiaContextValidator {

    EmissionsMonitoringPlanValidationResult validate(@Valid EmissionsMonitoringPlanCorsiaContainer empContainer);
}
