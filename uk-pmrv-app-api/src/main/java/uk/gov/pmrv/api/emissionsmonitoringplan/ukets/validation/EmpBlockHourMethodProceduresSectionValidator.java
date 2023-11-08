package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;

@Service
public class EmpBlockHourMethodProceduresSectionValidator extends EmpMethodProceduresSectionValidator {
    @Override
    protected EmpUkEtsSection getMethodProceduresSection(EmissionsMonitoringPlanUkEts emissionsMonitoringPlan) {
        return emissionsMonitoringPlan.getBlockHourMethodProcedures();
    }

    @Override
    protected FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod() {
        return FuelConsumptionMeasuringMethod.BLOCK_HOUR;
    }

    @Override
    protected EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation() {
        return EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_HOUR_METHOD_PROCEDURES;
    }

    @Override
    protected String getEmissionMonitoringPlanSectionName() {
        return EmpBlockHourMethodProcedures.class.getSimpleName();
    }
}
