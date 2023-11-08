package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;

@Service
public class EmpBlockOnBlockOffMethodProceduresSectionValidator extends EmpMethodProceduresSectionValidator {

    @Override
    protected EmpUkEtsSection getMethodProceduresSection(EmissionsMonitoringPlanUkEts emissionsMonitoringPlan) {
        return emissionsMonitoringPlan.getBlockOnBlockOffMethodProcedures();
    }

    @Override
    protected FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod() {
        return FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF;
    }

    @Override
    protected EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation() {
        return EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_ON_BLOCK_OFF_METHOD_PROCEDURES;
    }

    @Override
    protected String getEmissionMonitoringPlanSectionName() {
        return EmpBlockOnBlockOffMethodProcedures.class.getSimpleName();
    }
}
