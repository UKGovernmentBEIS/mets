package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaFuelUpliftMethodProceduresSectionValidatorTest {

    private final EmpCorsiaFuelUpliftMethodProceduresSectionValidator fuelUpliftMethodProceduresSectionValidator =
            new EmpCorsiaFuelUpliftMethodProceduresSectionValidator();

    @Test
    void validate_fuel_uplift_method_valid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build()))
                                .build())
                        .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder()
                                .blockHoursPerFlight(createEmpProcedureForm())
                                .zeroFuelUplift("zero fuel uplift")
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = fuelUpliftMethodProceduresSectionValidator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_no_fuel_uplift_method_valid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build()))
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = fuelUpliftMethodProceduresSectionValidator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void when_fuel_uplift_method_no_fuel_uplift_method_procedures_invalid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build()))
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = fuelUpliftMethodProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_UPLIFT_METHOD_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpFuelUpliftMethodProcedures.class.getSimpleName());
    }

    @Test
    void when_no_fuel_uplift_method_and_fuel_uplift_method_procedures_exist_invalid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build()))
                                .build())
                        .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder()
                                .blockHoursPerFlight(createEmpProcedureForm())
                                .zeroFuelUplift("zero fuel uplift")
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = fuelUpliftMethodProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_UPLIFT_METHOD_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpFuelUpliftMethodProcedures.class.getSimpleName());
    }

    @Test
    void when_small_emitters_fuel_uplift_method_procedures_exist_invalid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .build()))
                                .build())
                        .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder()
                                .blockHoursPerFlight(createEmpProcedureForm())
                                .zeroFuelUplift("zero fuel uplift")
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = fuelUpliftMethodProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_UPLIFT_METHOD_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpFuelUpliftMethodProcedures.class.getSimpleName());
    }

    private EmpProcedureForm createEmpProcedureForm() {
        return EmpProcedureForm.builder()
                .procedureReference("procedure reference")
                .procedureDocumentName("procedure document name")
                .procedureDescription("procedure description")
                .responsibleDepartmentOrRole("responsible role")
                .locationOfRecords("records location")
                .itSystemUsed("IT system")
                .build();
    }

}
