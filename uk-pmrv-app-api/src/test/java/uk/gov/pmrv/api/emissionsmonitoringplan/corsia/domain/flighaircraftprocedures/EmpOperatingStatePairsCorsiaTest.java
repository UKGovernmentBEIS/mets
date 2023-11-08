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
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;

@ExtendWith(MockitoExtension.class)
public class EmpOperatingStatePairsCorsiaTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA("stateA")
                .stateB("stateB")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_stateA_blank_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA("")
                .stateB("stateB")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_stateA_null_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA(null)
                .stateB("stateB")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_stateA_missing_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateB("stateB")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_stateB_blank_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA("stateA")
                .stateB("")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_stateB_null_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA("stateA")
                .stateB(null)
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_stateB_missing_invalid() {

        final EmpOperatingStatePairsCorsia aggregatedEmissionsData = EmpOperatingStatePairsCorsia.builder()
            .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                .stateA("stateA")
                .build()))
            .build();

        final Set<ConstraintViolation<EmpOperatingStatePairsCorsia>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }
}
