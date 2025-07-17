package uk.gov.pmrv.api.aviationreporting.ukets.domain.aircraftdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;

@ExtendWith(MockitoExtension.class)
class AviationAerAircraftDataTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("icao")
            .subType("subType")
            .ownerOrLessor("owner")
            .registrationNumber("registrationNr")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_invalid_aircraftTypeDesignator() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("")
            .subType("subType")
            .ownerOrLessor("owner")
            .registrationNumber("registrationNr")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("must not be blank")));
    }

    @Test
    void validate_invalid_registrationNumber() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("icao")
            .subType("subType")
            .ownerOrLessor("owner")
            .registrationNumber("registrationNrTooLongRegistrationNrTooLong")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("size must be between 0 and 20")));
    }

    @Test
    void validate_invalid_ownerOrLessor() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("icao")
            .subType("subType")
            .ownerOrLessor("")
            .registrationNumber("registrationNr")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("must not be blank")));
    }

    @Test
    void validate_invalid_startDate() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("icao")
            .subType("subType")
            .ownerOrLessor("owner")
            .registrationNumber("registrationNr")
            .startDate(null)
            .endDate(LocalDate.now())
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("must not be null")));
    }

    @Test
    void validate_invalid_endDate() {

        final AviationAerAircraftDataDetails aircraftDataDetails = AviationAerAircraftDataDetails.builder()
            .aircraftTypeDesignator("icao")
            .subType("subType")
            .ownerOrLessor("owner")
            .registrationNumber("registrationNr")
            .startDate(LocalDate.now())
            .endDate(null)
            .build();

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(Set.of(aircraftDataDetails))
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("must not be null")));
    }

    @Test
    void validate_invalid_aviationAerAircraftDataDetails() {

        final AviationAerAircraftData aircraftData = AviationAerAircraftData.builder()
            .aviationAerAircraftDataDetails(new HashSet<>())
            .build();

        final Set<ConstraintViolation<AviationAerAircraftData>> violations = validator.validate(aircraftData);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(violation -> violation.getMessage().contains("must not be empty")));
    }
}
