package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;

import java.util.ArrayList;
import java.util.List;

@Service
public abstract class EmpMethodProceduresSectionValidator implements EmpUkEtsContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanUkEtsContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();
        final EmpEmissionsMonitoringApproach emissionsMonitoringApproach =
                empContainer.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach();
        final EmissionsMonitoringApproachType monitoringApproachType = emissionsMonitoringApproach.getMonitoringApproachType();
        final EmpEmissionSources emissionSources = empContainer.getEmissionsMonitoringPlan().getEmissionSources();

        if (monitoringApproachType.equals(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)) {
            if ((checkFuelConsumptionMeasuringMethodExistence(emissionSources) && getMethodProceduresSection(empContainer.getEmissionsMonitoringPlan()) == null) ||
                    (!checkFuelConsumptionMeasuringMethodExistence(emissionSources) && getMethodProceduresSection(empContainer.getEmissionsMonitoringPlan()) != null)) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        getEmissionMonitoringPlanSectionName(),
                        getEmissionsMonitoringPlanViolation()));
            }
        } else {
            if (getMethodProceduresSection(empContainer.getEmissionsMonitoringPlan()) != null) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        getEmissionMonitoringPlanSectionName(),
                        getEmissionsMonitoringPlanViolation()));
            }
        }

        return EmissionsMonitoringPlanValidationResult.builder()
                .valid(empViolations.isEmpty())
                .empViolations(empViolations)
                .build();
    }

    private boolean checkFuelConsumptionMeasuringMethodExistence(EmpEmissionSources emissionSources) {
        return emissionSources.getAircraftTypes().stream()
                .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
                .anyMatch(getFuelConsumptionMeasuringMethod()::equals);
    }

    protected abstract EmpUkEtsSection getMethodProceduresSection(EmissionsMonitoringPlanUkEts emissionsMonitoringPlan);

    protected abstract FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod();
    protected abstract EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation();

    protected abstract String getEmissionMonitoringPlanSectionName();
}
