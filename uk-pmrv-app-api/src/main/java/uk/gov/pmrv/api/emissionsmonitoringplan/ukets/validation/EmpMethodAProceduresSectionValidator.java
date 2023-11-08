package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;

@Service
public class EmpMethodAProceduresSectionValidator extends EmpMethodProceduresSectionValidator {

    @Override
    protected EmpUkEtsSection getMethodProceduresSection(EmissionsMonitoringPlanUkEts emissionsMonitoringPlan) {
        return emissionsMonitoringPlan.getMethodAProcedures();
    }

    @Override
    protected FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod() {
        return FuelConsumptionMeasuringMethod.METHOD_A;
    }

    @Override
    protected EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation() {
        return EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_A_PROCEDURES;
    }

    @Override
    protected String getEmissionMonitoringPlanSectionName() {
        return EmpMethodAProcedures.class.getSimpleName();
    }
}
