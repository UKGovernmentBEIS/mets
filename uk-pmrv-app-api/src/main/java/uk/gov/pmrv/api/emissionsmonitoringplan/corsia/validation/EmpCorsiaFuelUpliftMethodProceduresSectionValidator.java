package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Service
public class EmpCorsiaFuelUpliftMethodProceduresSectionValidator extends EmpCorsiaMethodProceduresSectionValidator {

    @Override
    protected EmpCorsiaSection getMethodProceduresSection(EmissionsMonitoringPlanCorsia emissionsMonitoringPlan) {
        return emissionsMonitoringPlan.getFuelUpliftMethodProcedures();
    }

    @Override
    protected FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod() {
        return FuelConsumptionMeasuringMethod.FUEL_UPLIFT;
    }

    @Override
    protected EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation() {
        return EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_UPLIFT_METHOD_PROCEDURES;
    }

    @Override
    protected String getEmissionMonitoringPlanSectionName() {
        return EmpFuelUpliftMethodProcedures.class.getSimpleName();
    }
}
