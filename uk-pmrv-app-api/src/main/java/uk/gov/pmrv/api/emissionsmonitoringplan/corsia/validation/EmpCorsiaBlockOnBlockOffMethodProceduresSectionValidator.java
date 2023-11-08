package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Service
public class EmpCorsiaBlockOnBlockOffMethodProceduresSectionValidator extends EmpCorsiaMethodProceduresSectionValidator {

    @Override
    protected EmpCorsiaSection getMethodProceduresSection(EmissionsMonitoringPlanCorsia emissionsMonitoringPlan) {
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
