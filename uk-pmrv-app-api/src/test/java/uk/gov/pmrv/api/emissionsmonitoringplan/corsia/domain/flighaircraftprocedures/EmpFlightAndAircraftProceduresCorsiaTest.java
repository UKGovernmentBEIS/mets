package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flighaircraftprocedures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;

@ExtendWith(MockitoExtension.class)
class EmpFlightAndAircraftProceduresCorsiaTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_procedure_forms_operating_pairs_are_null_then_invalid() {
        final EmpFlightAndAircraftProceduresCorsia
            flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder().build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(6, violations.size());
    }

    @Test
    void when_procedure_forms_operating_pairs_valid_then_valid() {
        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";
        final EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder()
                .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                .operatingStatePairs(createOperatingStatePairs())
                .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                .internationalFlightsDeterminationNoMonitoring(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                .build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(0, violations.size());
    }

    @Test
    void when_procedure_mandatory_fields_missing_then_invalid() {
        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";
        final EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder()
            .aircraftUsedDetails(createProcedureForm(null, null, null,
                null, null, itSystem))
            .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .operatingStatePairs(createOperatingStatePairs())
            .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDeterminationNoMonitoring(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(5, violations.size());
    }

    @Test
    void when_procedure_mandatory_fields_blank_then_invalid() {
        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";
        final EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder()
            .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .operatingStatePairs(createOperatingStatePairs())
            .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDeterminationNoMonitoring(createProcedureForm("", "", "",
                "", "", itSystem))
            .build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(5, violations.size());
    }

    @Test
    void when_procedure_IT_system_missing_then_valid() {
        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";
        final EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder()
            .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .operatingStatePairs(createOperatingStatePairs())
            .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDeterminationNoMonitoring(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, null))
            .build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(0, violations.size());
    }

    @Test
    void when_procedure_IT_system_blank_then_valid() {
        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";
        final EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures = EmpFlightAndAircraftProceduresCorsia.builder()
            .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .operatingStatePairs(createOperatingStatePairs())
            .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, itSystem))
            .internationalFlightsDeterminationNoMonitoring(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                responsibleDepartment, locationOfRecords, ""))
            .build();

        final Set<ConstraintViolation<EmpFlightAndAircraftProceduresCorsia>> violations = validator.validate(flightAndAircraftProcedures);

        assertEquals(0, violations.size());
    }

    private EmpOperatingStatePairsCorsia createOperatingStatePairs() {
        return EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(
                EmpOperatingStatePairsCorsiaDetails.builder()
                    .stateA("State1")
                    .stateB("State2")
                    .build(),
                EmpOperatingStatePairsCorsiaDetails.builder()
                    .stateA("State3")
                    .stateB("State4")
                    .build()
            ))
            .build();
    }

    private EmpProcedureForm createProcedureForm(String procedureDescription, String procedureDocumentName, String procedureReference,
                                                 String responsibleDepartment, String locationOfRecords, String itSystem) {
        return EmpProcedureForm.builder()
                .procedureDescription(procedureDescription)
                .procedureDocumentName(procedureDocumentName)
                .procedureReference(procedureReference)
                .responsibleDepartmentOrRole(responsibleDepartment)
                .locationOfRecords(locationOfRecords)
                .itSystemUsed(itSystem)
                .build();
    }
}
