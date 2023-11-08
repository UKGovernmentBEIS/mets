package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;

@ExtendWith(MockitoExtension.class)
public class EmpCorsiaEmissionSourcesSectionValidatorTest {

    @InjectMocks
    private EmpCorsiaEmissionSourcesSectionValidator validator;

    @Test
    void validate_fuel_use_valid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(
                    FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                        .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(
                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)))
                    .multipleFuelConsumptionMethodsExplanation("multiple fuel explanation")
                    .build())
                .build())
            .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertTrue(result.isValid());
        assertThat(result.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_fuel_use_invalid_due_to_missing_method() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(
                    FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                        .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(
                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)))
                    .multipleFuelConsumptionMethodsExplanation("multiple fuel explanation")
                    .build())
                .build())
            .build();

        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertEquals(1, result.getEmpViolations().size());
    }

    @Test
    void validate_fuel_use_invalid_due_to_missing_methods() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(
                    FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                        .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(
                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)))
                    .multipleFuelConsumptionMethodsExplanation("multiple fuel explanation")
                    .build())
                .build())
            .build();

        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertEquals(2, result.getEmpViolations().size());
    }

    @Test
    void validate_fuel_use_due_to_cert_approach() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(
                    FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
                        .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(
                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)))
                    .multipleFuelConsumptionMethodsExplanation("multiple fuel explanation")
                    .build())
                .build())
            .build();

        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertTrue(result.isValid());
        assertEquals(0, result.getEmpViolations().size());
    }

    private AircraftTypeDetailsCorsia createAircraftTypeDetails(String manufacturer, String model, String designator, FuelConsumptionMeasuringMethod method) {
        return AircraftTypeDetailsCorsia.builder()
            .aircraftTypeInfo(AircraftTypeInfo.builder()
                .manufacturer(manufacturer)
                .model(model)
                .designatorType(designator)
                .build())
            .fuelConsumptionMeasuringMethod(method)
            .build();
    }
}