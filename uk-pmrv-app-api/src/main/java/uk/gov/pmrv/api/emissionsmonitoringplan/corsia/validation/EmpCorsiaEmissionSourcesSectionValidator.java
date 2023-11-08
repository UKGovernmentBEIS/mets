package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;

@Service
public class EmpCorsiaEmissionSourcesSectionValidator implements EmpCorsiaContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanCorsiaContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();
        final EmpEmissionsMonitoringApproachCorsia emissionsMonitoringApproach =
                empContainer.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach();
        final EmissionsMonitoringApproachTypeCorsia monitoringApproachType = emissionsMonitoringApproach.getMonitoringApproachType();

        if (EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING.equals(monitoringApproachType)) {
            Set<AircraftTypeDetailsCorsia> aircraftTypes = empContainer.getEmissionsMonitoringPlan().getEmissionSources().getAircraftTypes();

            for (AircraftTypeDetailsCorsia aircraftType : aircraftTypes) {
                if (aircraftType.getFuelConsumptionMeasuringMethod() == null) {
                    empViolations.add(new EmissionsMonitoringPlanViolation(
                        AircraftTypeDetailsCorsia.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD
                    ));
                }
            }
        }
        return EmissionsMonitoringPlanValidationResult.builder()
                .valid(empViolations.isEmpty())
                .empViolations(empViolations)
                .build();
    }
}
