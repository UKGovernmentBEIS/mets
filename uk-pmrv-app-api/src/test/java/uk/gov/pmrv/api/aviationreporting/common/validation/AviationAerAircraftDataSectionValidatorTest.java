package uk.gov.pmrv.api.aviationreporting.common.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerAircraftDataSectionValidatorTest {

    @InjectMocks
    private AviationAerAircraftDataSectionValidator validator;

    @Mock
    private AircraftTypeQueryService aircraftTypeQueryService;

    @Test
    void validate_invalid_designators() {

        final AviationAerAircraftData aviationAerAircraftData = AviationAerAircraftData.builder()
                .aviationAerAircraftDataDetails(Set.of(AviationAerAircraftDataDetails.builder()
                                .aircraftTypeDesignator("icao")
                                .subType("subType")
                                .ownerOrLessor("owner")
                                .registrationNumber("registrationNr1")
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now())
                                .build(),
                        AviationAerAircraftDataDetails.builder()
                                .aircraftTypeDesignator("icao")
                                .subType("subType")
                                .ownerOrLessor("owner")
                                .registrationNumber("registrationNr2")
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now())
                                .build()
                ))
                .build();

        when(aircraftTypeQueryService.findInvalidDesignatorCodes(Mockito.anyList())).thenReturn(List.of("icao"));
        final AviationAerValidationResult actual = validator.validateAircaftData(aviationAerAircraftData.getAviationAerAircraftDataDetails(),
                Year.of(LocalDate.now().getYear()));
        assertFalse(actual.isValid());
        Assertions.assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_AIRCRAFT_DATA_DESIGNATOR.getMessage());
    }

    @Test
    void validate_start_date_after_end_date() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1);

        final AviationAerAircraftData aviationAerAircraftData = AviationAerAircraftData.builder()
                .aviationAerAircraftDataDetails(Set.of(AviationAerAircraftDataDetails.builder()
                        .aircraftTypeDesignator("icao")
                        .subType("subType")
                        .ownerOrLessor("owner")
                        .registrationNumber("registrationNr")
                        .startDate(startDate)
                        .endDate(endDate)
                        .build()))
                .build();


        when(aircraftTypeQueryService.findInvalidDesignatorCodes(Mockito.anyList())).thenReturn(Collections.emptyList());
        AviationAerValidationResult actual = validator.validateAircaftData(aviationAerAircraftData.getAviationAerAircraftDataDetails(),
                Year.of(LocalDate.now().getYear()));
        assertFalse(actual.isValid());
        Assertions.assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_INTERVAL.getMessage());
    }

    @Test
    void validate_dates_in_different_scheme_years() {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();

        final AviationAerAircraftData aviationAerAircraftData = AviationAerAircraftData.builder()
                .aviationAerAircraftDataDetails(Set.of(AviationAerAircraftDataDetails.builder()
                        .aircraftTypeDesignator("icao")
                        .subType("subType")
                        .ownerOrLessor("owner")
                        .registrationNumber("registrationNr")
                        .startDate(startDate)
                        .endDate(endDate)
                        .build()))
                .build();

        when(aircraftTypeQueryService.findInvalidDesignatorCodes(Mockito.anyList())).thenReturn(Collections.emptyList());
        AviationAerValidationResult actual = validator.validateAircaftData(aviationAerAircraftData.getAviationAerAircraftDataDetails(),
                Year.of(LocalDate.now().getYear()));
        assertFalse(actual.isValid());
        Assertions.assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_END_DATE_SCHEME_YEAR.getMessage());
    }
}
