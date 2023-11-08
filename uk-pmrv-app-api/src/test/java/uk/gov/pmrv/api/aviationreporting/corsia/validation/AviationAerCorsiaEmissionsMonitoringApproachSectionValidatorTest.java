package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaAircraftTypeDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaCertDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFlightType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFuelDensityType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFuelUseMonitoringDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaEmissionsMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaEmissionsMonitoringApproachSectionValidator validator;

    @Mock
    private AircraftTypeQueryService aircraftTypeQueryService;

    @Test
    void when_all_international_flights_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.ALL_INTERNATIONAL_FLIGHTS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_not_all_international_flights_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_NO_OFFSETTING_OBLIGATIONS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                                        .fuelDensityType(AviationAerCorsiaFuelDensityType.NOT_APPLICABLE)
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_no_cert_used_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.FALSE)
                                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_DENSITY)
                                        .identicalToProcedure(Boolean.TRUE)
                                        .blockHourUsed(Boolean.FALSE)
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_all_international_flights_invalid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.ALL_INTERNATIONAL_FLIGHTS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                                        .fuelDensityType(AviationAerCorsiaFuelDensityType.NOT_APPLICABLE)
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations().get(0).getMessage())
                .isEqualTo(AviationAerViolation.AviationAerViolationMessage.INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION.getMessage());
    }

    @Test
    void when_not_all_international_flights_invalid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations().get(0).getMessage())
                .isEqualTo(AviationAerViolation.AviationAerViolationMessage.INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION.getMessage());
    }

    @Test
    void when_aircraft_type_details_exist_valid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_DENSITY)
                                        .identicalToProcedure(Boolean.TRUE)
                                        .blockHourUsed(Boolean.TRUE)
                                        .aircraftTypeDetails(Set.of(
                                                createAircraftTypeDetails("designator1", "subtype1", BigDecimal.valueOf(5.12)),
                                                createAircraftTypeDetails("designator2", "subtype2", BigDecimal.valueOf(6.123))
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();

        when(aircraftTypeQueryService.findInvalidDesignatorCodes(List.of("designator1", "designator2"))).thenReturn(Collections.emptyList());
        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void when_aircraft_type_details_exist_invalid() {

        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                                .certUsed(Boolean.TRUE)
                                .certDetails(AviationAerCorsiaCertDetails.builder()
                                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                                        .publicationYear(Year.of(2022))
                                        .build())
                                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_DENSITY)
                                        .identicalToProcedure(Boolean.TRUE)
                                        .blockHourUsed(Boolean.TRUE)
                                        .aircraftTypeDetails(Set.of(
                                                createAircraftTypeDetails("designator1", "subtype1", BigDecimal.valueOf(5.12)),
                                                createAircraftTypeDetails("designator2", "subtype2", BigDecimal.valueOf(6.123))
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();

        when(aircraftTypeQueryService.findInvalidDesignatorCodes(List.of("designator1", "designator2"))).thenReturn(List.of("designator2"));
        final AviationAerValidationResult actual = validator.validate(aerContainer);

        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations().get(0).getMessage())
                .isEqualTo(AviationAerViolation.AviationAerViolationMessage.INVALID_AIRCRAFT_DATA_DESIGNATOR.getMessage());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getData).hasSize(1);
    }

    private AviationAerCorsiaAircraftTypeDetails createAircraftTypeDetails(String designator, String subtype, BigDecimal fuelBurnRatio) {
        return AviationAerCorsiaAircraftTypeDetails.builder()
                .designator(designator)
                .subtype(subtype)
                .fuelBurnRatio(fuelBurnRatio)
                .build();
    }

}
