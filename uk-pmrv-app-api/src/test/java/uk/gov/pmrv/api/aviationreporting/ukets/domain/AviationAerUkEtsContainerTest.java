package uk.gov.pmrv.api.aviationreporting.ukets.domain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.CustomLocalValidatorFactoryBean;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerMonitoringPlanChanges;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerSupportFacilityMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.TotalEmissionsType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissionsConfidentiality;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.StandardFuelsTotalEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;
import uk.gov.netz.api.referencedata.service.CountryValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsContainerTest {

    private final CountryService countryService = mock(CountryService.class);
    private final List<ConstraintValidator<?, ?>> customConstraintValidators =
            Collections.singletonList(new CountryValidator(countryService));
    private Validator validator;


    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = new CustomLocalValidatorFactoryBean(customConstraintValidators)) {
            validator = factory.getValidator();
        }
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
    }

    @Test
    void when_emissions_report_required_true_then_valid() {

        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.TRUE)
                .aer(createAer())
                .submittedEmissions(createSubmittedEmissions())
                .serviceContactDetails(buildServiceContactDetails())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(0, violations.size());
    }

    @Test
    void when_emissions_report_required_true_then_invalid() {
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.TRUE)
                .serviceContactDetails(buildServiceContactDetails())
                .submittedEmissions(createSubmittedEmissions())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.reportingObligation.reportingRequired}");
    }

    @Test
    void when_emissions_report_required_false_then_valid() {
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.FALSE)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                .serviceContactDetails(buildServiceContactDetails())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(0, violations.size());
    }

    @Test
    void when_emissions_report_required_false_then_invalid() {
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.FALSE)
                .serviceContactDetails(buildServiceContactDetails())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.reportingObligation.reportingObligationDetails}");
    }

    @Test
    void when_emissions_report_required_empty_reason_invalid() {
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.FALSE)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().build())
                .serviceContactDetails(buildServiceContactDetails())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(1, violations.size());
    }

    @Test
    void when_emissions_report_required_false_aer_not_null_then_invalid() {
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .reportingYear(Year.of(2023))
                .reportingRequired(Boolean.FALSE)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                .aer(createAer())
                .serviceContactDetails(buildServiceContactDetails())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsContainer>> violations = validator.validate(aerContainer);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{aviationAer.reportingObligation.reportingRequired}");
    }

    private AviationAerUkEts createAer() {

        return AviationAerUkEts.builder()
                .aerMonitoringPlanChanges(AviationAerMonitoringPlanChanges.builder()
                        .notCoveredChangesExist(Boolean.FALSE)
                        .build())
                .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(false)
                        .build())
                .operatorDetails(AviationOperatorDetails.builder()
                        .operatorName("operator name")
                        .crcoCode("crco code")
                        .airOperatingCertificate(AirOperatingCertificate.builder()
                                .certificateExist(Boolean.TRUE)
                                .certificateNumber("certificate number")
                                .issuingAuthority("issuing authority")
                                .certificateFiles(Set.of(UUID.randomUUID()))
                                .build())
                        .operatingLicense(OperatingLicense.builder()
                                .licenseExist(Boolean.TRUE)
                                .licenseNumber("license number")
                                .issuingAuthority("issuing authority")
                                .build())
                        .flightIdentification(FlightIdentification.builder()
                                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                .icaoDesignators("icao designators")
                                .build())
                        .organisationStructure(PartnershipOrganisation.builder()
                                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                                .organisationLocation(LocationOnShoreStateDTO.builder()
                                        .type(LocationType.ONSHORE_STATE)
                                        .line1("line1")
                                        .city("city")
                                        .country("GR")
                                        .build())
                                .partnershipName("partnership name")
                                .partners(Set.of("partner 1", "partner 2"))
                                .build())
                        .build())
                .monitoringApproach(AviationAerSupportFacilityMonitoringApproach.builder()
                        .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                        .totalEmissionsType(TotalEmissionsType.FULL_SCOPE_FLIGHTS)
                        .fullScopeTotalEmissions(BigDecimal.valueOf(1234.56))
                        .build())
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                .airportFrom(AviationRptAirportsDTO.builder()
                                        .icao("icaoFrom")
                                        .name("nameFrom")
                                        .country("countryFrom")
                                        .countryType(CountryType.EEA_COUNTRY)
                                        .state("state")
                                        .build())
                                .airportTo(AviationRptAirportsDTO.builder()
                                        .icao("icaoTo")
                                        .name("nameTo")
                                        .country("countryTo")
                                        .countryType(CountryType.EEA_COUNTRY)
                                        .state("state")
                                        .build())
                                .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                .fuelConsumption(BigDecimal.valueOf(123.45))
                                .flightsNumber(10)
                                .build()))
                        .build())
                .dataGaps(AviationAerDataGaps.builder()
                        .exist(Boolean.FALSE)
                        .build())
                .saf(AviationAerSaf.builder()
                        .exist(Boolean.FALSE)
                        .build())
                .aviationAerTotalEmissionsConfidentiality(AviationAerTotalEmissionsConfidentiality.builder()
                        .confidential(Boolean.FALSE)
                        .build())
                .aviationAerAircraftData(AviationAerAircraftData.builder()
                        .aviationAerAircraftDataDetails(Set.of(AviationAerAircraftDataDetails.builder()
                                .aircraftTypeDesignator("icao")
                                .subType("subType")
                                .ownerOrLessor("owner")
                                .registrationNumber("registrationNr")
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now())
                                .build()))
                        .build())
                .build();
    }

    private AviationAerUkEtsSubmittedEmissions createSubmittedEmissions() {
        AviationAerTotalEmissions expectedResponse = AviationAerTotalEmissions.builder()
                .numFlightsCoveredByUkEts(30)
                .standardFuelEmissions(new BigDecimal("9350.00"))
                .reductionClaimEmissions(new BigDecimal("4725"))
                .totalEmissions(new BigDecimal("4625"))
                .build();
        final AerodromePairsTotalEmissions aerodromePairsTotalEmissions = AerodromePairsTotalEmissions.builder()
                .departureAirport(AviationRptAirportsDTO.builder()
                        .icao("ICAO1")
                        .name("name1")
                        .country("country1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state")
                        .build())
                .arrivalAirport(AviationRptAirportsDTO.builder()
                        .icao("ICAO2")
                        .name("name2")
                        .country("country2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .state("state")
                        .build())
                .flightsNumber(20)
                .emissions(BigDecimal.valueOf(123.456))
                .build();
        final AviationAerDomesticFlightsEmissions domesticFlightsEmissions = AviationAerDomesticFlightsEmissions.builder()
                .domesticFlightsEmissionsDetails(List.of(AviationAerDomesticFlightsEmissionsDetails.builder()
                        .country("United Kingdom")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                .flightsNumber(20)
                                .fuelConsumption(BigDecimal.valueOf(100.00))
                                .emissions(BigDecimal.valueOf(310.00))
                                .build())
                        .build()))
                .totalEmissions(BigDecimal.valueOf(310.00))
                .build();
        return AviationAerUkEtsSubmittedEmissions.builder()
                .aviationAerTotalEmissions(expectedResponse)
                .standardFuelsTotalEmissions(List.of(
                        StandardFuelsTotalEmissions.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                .emissionsFactor(AviationAerUkEtsFuelType.JET_KEROSENE.getEmissionFactor())
                                .netCalorificValue(AviationAerUkEtsFuelType.JET_KEROSENE.getNetCalorificValue())
                                .fuelConsumption(BigDecimal.valueOf(100.0))
                                .emissions(BigDecimal.valueOf(315.0))
                                .build()
                ))
                .aerodromePairsTotalEmissions(List.of(aerodromePairsTotalEmissions))
                .domesticFlightsEmissions(domesticFlightsEmissions)
                .build();
    }

    private ServiceContactDetails buildServiceContactDetails() {
        return ServiceContactDetails.builder()
            .name("name")
            .email("email@email.uk")
            .roleCode("code")
            .build();
    }
}
