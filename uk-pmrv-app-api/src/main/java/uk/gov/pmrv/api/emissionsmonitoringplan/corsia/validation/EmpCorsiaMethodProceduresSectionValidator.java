package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;

@Service
public abstract class EmpCorsiaMethodProceduresSectionValidator implements EmpCorsiaContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanCorsiaContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();
        final EmpEmissionsMonitoringApproachCorsia emissionsMonitoringApproach =
                empContainer.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach();
        final EmissionsMonitoringApproachTypeCorsia monitoringApproachType = emissionsMonitoringApproach.getMonitoringApproachType();
        final EmpEmissionSourcesCorsia emissionSources = empContainer.getEmissionsMonitoringPlan().getEmissionSources();

        if (monitoringApproachType.equals(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)) {
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

    private boolean checkFuelConsumptionMeasuringMethodExistence(EmpEmissionSourcesCorsia emissionSources) {
        return emissionSources.getAircraftTypes().stream()
                .map(AircraftTypeDetailsCorsia::getFuelConsumptionMeasuringMethod)
                .anyMatch(getFuelConsumptionMeasuringMethod()::equals);
    }

    protected abstract EmpCorsiaSection getMethodProceduresSection(EmissionsMonitoringPlanCorsia emissionsMonitoringPlan);

    protected abstract FuelConsumptionMeasuringMethod getFuelConsumptionMeasuringMethod();
    protected abstract EmissionsMonitoringPlanViolation.ViolationMessage getEmissionsMonitoringPlanViolation();

    protected abstract String getEmissionMonitoringPlanSectionName();
}
