package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EmpEmissionSourcesSectionValidator implements EmpUkEtsContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanUkEtsContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();
        final EmpEmissionsMonitoringApproach emissionsMonitoringApproach =
                empContainer.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach();
        final EmissionsMonitoringApproachType monitoringApproachType = emissionsMonitoringApproach.getMonitoringApproachType();
        final EmpEmissionSources emissionSources = empContainer.getEmissionsMonitoringPlan().getEmissionSources();

        if (EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType)) {
            if (checkFuelConsumptionMethodExistence(emissionSources, Objects::isNull)) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD));
            }
            if (emissionSources.getAdditionalAircraftMonitoringApproach() == null) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH));
            }
            if ((checkForMultipleFuelConsumptionMethods(emissionSources) &&
                    (ObjectUtils.isEmpty(emissionSources.getMultipleFuelConsumptionMethodsExplanation())))
                    || (!checkForMultipleFuelConsumptionMethods(emissionSources) && !ObjectUtils.isEmpty(emissionSources.getMultipleFuelConsumptionMethodsExplanation()))) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION));
            }
        } else {
            if (checkFuelConsumptionMethodExistence(emissionSources, Objects::nonNull)) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD));
            }
            if (emissionSources.getAdditionalAircraftMonitoringApproach() != null) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH));
            }
            if (emissionSources.getMultipleFuelConsumptionMethodsExplanation() != null) {
                empViolations.add(new EmissionsMonitoringPlanViolation(
                        EmpEmissionSources.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION));
            }
        }
        return EmissionsMonitoringPlanValidationResult.builder()
                .valid(empViolations.isEmpty())
                .empViolations(empViolations)
                .build();
    }

    private boolean checkFuelConsumptionMethodExistence(EmpEmissionSources emissionSources, Predicate<FuelConsumptionMeasuringMethod> predicate) {
        return emissionSources.getAircraftTypes().stream()
                .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
                .anyMatch(predicate);
    }

    private boolean checkForMultipleFuelConsumptionMethods(EmpEmissionSources emissionSources) {
        final Set<FuelConsumptionMeasuringMethod> fuelConsumptionMeasuringMethods = emissionSources.getAircraftTypes().stream()
                .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return fuelConsumptionMeasuringMethods.size() > 1;
    }
}
