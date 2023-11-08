package uk.gov.pmrv.api.aviationreporting.ukets.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.dto.AviationAerEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDepartureArrivalStateFuelUsedTriplet;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.StandardFuelsTotalEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AviationAerUkEtsSubmittedEmissionsCalculationService implements AviationAerSubmittedEmissionsCalculationService<AviationAerUkEtsContainer, AviationAerUkEtsSubmittedEmissions> {

    public BigDecimal calculateTotalSubmittedEmissions(AviationAerUkEts aer) {
        AviationAerEmissionsCalculationDTO calculationDTO = AviationAerEmissionsCalculationDTO.builder()
            .aggregatedEmissionsData(aer.getAggregatedEmissionsData())
            .saf(aer.getSaf())
            .build();

        AviationAerTotalEmissions totalEmissions = calculateTotalEmissions(calculationDTO);

        return totalEmissions.getTotalEmissions();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }

    @Override
    public AviationAerUkEtsSubmittedEmissions calculateSubmittedEmissions(AviationAerUkEtsContainer aerContainer) {
        AviationAerUkEts aer = aerContainer.getAer();
        AviationAerEmissionsCalculationDTO calculationDTO = AviationAerEmissionsCalculationDTO.builder()
            .aggregatedEmissionsData(aer.getAggregatedEmissionsData())
            .saf(aer.getSaf())
            .build();

        return AviationAerUkEtsSubmittedEmissions.builder()
            .aviationAerTotalEmissions(calculateTotalEmissions(calculationDTO))
            .standardFuelsTotalEmissions(calculateStandardFuelsTotalEmissions(calculationDTO))
            .aerodromePairsTotalEmissions(calculateAerodromePairsTotalEmissions(calculationDTO))
            .domesticFlightsEmissions(calculateDomesticFlightsEmissions(calculationDTO))
            .nonDomesticFlightsEmissions(calculateNonDomesticFlightsEmissions(calculationDTO))
            .build();
    }

    /**
     * Calculate total emissions based on input data.
     *
     * @param aviationAerEmissionsCalculationDTO Input data for AviationAerEmissionsCalculationDTO.
     * @return Calculated total emissions.
     */
    public AviationAerTotalEmissions calculateTotalEmissions(AviationAerEmissionsCalculationDTO
                                                                     aviationAerEmissionsCalculationDTO) {
        Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        int numFlightsCoveredByUkEts = 0;
        BigDecimal emissionsFromStandardFuels = BigDecimal.ZERO;

        for (AviationAerUkEtsAggregatedEmissionDataDetails emissionDataDetails : aggregatedEmissionDataDetails) {
            numFlightsCoveredByUkEts += emissionDataDetails.getFlightsNumber();
            BigDecimal emissionFactor = emissionDataDetails.getFuelType().getEmissionFactor();
            emissionsFromStandardFuels = emissionsFromStandardFuels.add(emissionDataDetails.getFuelConsumption()
                            .multiply(emissionFactor));
        }

        AviationAerSaf aviationAerSaf = aviationAerEmissionsCalculationDTO.getSaf();
        BigDecimal emissionsReductionClaimForTheSchemeYear = Boolean.TRUE.equals(aviationAerSaf.getExist())
                ? aviationAerSaf.getSafDetails().getTotalEmissionsReductionClaim()
                : BigDecimal.ZERO;

        BigDecimal totalEmissionsForTheSchemeYear = emissionsFromStandardFuels.subtract(emissionsReductionClaimForTheSchemeYear).max(BigDecimal.ZERO);

        return AviationAerTotalEmissions.builder()
                .numFlightsCoveredByUkEts(numFlightsCoveredByUkEts)
                .standardFuelEmissions(emissionsFromStandardFuels.setScale(3, RoundingMode.HALF_UP))
                .reductionClaimEmissions(emissionsReductionClaimForTheSchemeYear)
                .totalEmissions(totalEmissionsForTheSchemeYear.setScale(0, RoundingMode.HALF_UP))
                .build();
    }

    public List<StandardFuelsTotalEmissions> calculateStandardFuelsTotalEmissions(AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {

        List<StandardFuelsTotalEmissions> standardFuelsTotalEmissions = new ArrayList<>();
        final Map<AviationAerUkEtsFuelType, List<AviationAerUkEtsAggregatedEmissionDataDetails>> fuelTypeEmissionsDataMap =
                aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails().stream()
                        .collect(Collectors.groupingBy(AviationAerUkEtsAggregatedEmissionDataDetails::getFuelType));

        fuelTypeEmissionsDataMap.keySet()
                .forEach(fuelType -> standardFuelsTotalEmissions.add(calculateStandardFuelsTotalEmissions(fuelTypeEmissionsDataMap.get(fuelType))));
        return standardFuelsTotalEmissions;
    }

    public List<AerodromePairsTotalEmissions> calculateAerodromePairsTotalEmissions(AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {

        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> allAggregatedEmissionDataDetails =
                aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        List<AerodromePairsTotalEmissions> aerodromePairsTotalEmissions = new ArrayList<>();

        Map<AviationRptAirportsDTO, Map<AviationRptAirportsDTO, Set<AviationAerUkEtsAggregatedEmissionDataDetails>>> aggregatedEmissionsPerAirportPair =
                allAggregatedEmissionDataDetails.stream()
                        .collect(
                                Collectors.groupingBy(AviationAerUkEtsAggregatedEmissionDataDetails::getAirportFrom,
                                        Collectors.groupingBy(AviationAerUkEtsAggregatedEmissionDataDetails::getAirportTo, Collectors.toSet())));

        aggregatedEmissionsPerAirportPair.forEach((departureAirport, emissionsDataPerArrivalAirport) ->
                emissionsDataPerArrivalAirport.forEach((arrivalAirport, emissionsData) ->
                        aerodromePairsTotalEmissions.add(AerodromePairsTotalEmissions.builder()
                                .departureAirport(departureAirport)
                                .arrivalAirport(arrivalAirport)
                                .flightsNumber(calculateFlightsNumber(emissionsData.stream().toList()))
                                .emissions(emissionsData.stream()
                                        .map(this::calculateEmissions)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(3, RoundingMode.HALF_UP))
                                .build())));

        return aerodromePairsTotalEmissions;
    }

    public AviationAerDomesticFlightsEmissions calculateDomesticFlightsEmissions(AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {

        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final List<AviationAerUkEtsAggregatedEmissionDataDetails> domesticAggregatedEmissionsData = aggregatedEmissionDataDetails.stream()
                .filter(detail -> CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(detail.getAirportFrom().getCountryType()) &&
                        CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(detail.getAirportTo().getCountryType())).toList();

        final Map<AviationAerUkEtsFuelType, List<AviationAerUkEtsAggregatedEmissionDataDetails>> domesticAggregatedEmissionsDataPerFuelType = domesticAggregatedEmissionsData
                .stream()
                .collect(Collectors.groupingBy(AviationAerUkEtsAggregatedEmissionDataDetails::getFuelType));

        final List<AviationAerDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = domesticAggregatedEmissionsDataPerFuelType.keySet().stream()
                .map(fuelType -> calculateDomesticFlightsEmissionsDetails(domesticAggregatedEmissionsDataPerFuelType.get(fuelType), fuelType))
                .collect(Collectors.toList());

        BigDecimal totalEmissions = calculateTotalFlightsEmissions(domesticFlightsEmissionsDetails.stream()
                .map(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).collect(Collectors.toList()));

        return AviationAerDomesticFlightsEmissions.builder()
                .domesticFlightsEmissionsDetails(domesticFlightsEmissionsDetails)
                .totalEmissions(totalEmissions)
                .build();
    }

    public AviationAerNonDomesticFlightsEmissions calculateNonDomesticFlightsEmissions(AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {

        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final List<AviationAerUkEtsAggregatedEmissionDataDetails> nonDomesticAggregatedEmissionsData = aggregatedEmissionDataDetails.stream()
                .filter(detail -> !CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(detail.getAirportFrom().getCountryType()) ||
                        !CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(detail.getAirportTo().getCountryType())).toList();

        final Map<AviationAerDepartureArrivalStateFuelUsedTriplet, List<AviationAerUkEtsAggregatedEmissionDataDetails>> nonDomesticAggregatedEmissionsDataMap = nonDomesticAggregatedEmissionsData
                .stream()
                .collect(Collectors.groupingBy(aviationAerUkEtsAggregatedEmissionDataDetails -> AviationAerDepartureArrivalStateFuelUsedTriplet.builder()
                        .arrivalCountry(aviationAerUkEtsAggregatedEmissionDataDetails.getAirportTo().getCountry())
                        .departureCountry(aviationAerUkEtsAggregatedEmissionDataDetails.getAirportFrom().getCountry())
                        .fuelType(aviationAerUkEtsAggregatedEmissionDataDetails.getFuelType())
                        .build()));

        final List<AviationAerNonDomesticFlightsEmissionsDetails> nonDomesticFlightsEmissionsDetails = nonDomesticAggregatedEmissionsDataMap.entrySet().stream()
                .map(entry -> calculateNonDomesticFlightsEmissionsDetails(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());

        BigDecimal totalEmissions = calculateTotalFlightsEmissions(nonDomesticFlightsEmissionsDetails.stream()
                .map(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).collect(Collectors.toList()));

        return AviationAerNonDomesticFlightsEmissions.builder()
                .nonDomesticFlightsEmissionsDetails(nonDomesticFlightsEmissionsDetails)
                .totalEmissions(totalEmissions)
                .build();
    }

    private StandardFuelsTotalEmissions calculateStandardFuelsTotalEmissions(List<AviationAerUkEtsAggregatedEmissionDataDetails> aviationAerAggregatedEmissionDataDetails) {
        final BigDecimal fuelConsumption = aviationAerAggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getFuelConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final AviationAerUkEtsFuelType fuelType = aviationAerAggregatedEmissionDataDetails.get(0).getFuelType();
        final BigDecimal emissionFactor = fuelType.getEmissionFactor();
        final BigDecimal emissions = calculateEmissions(fuelConsumption, emissionFactor);
        return StandardFuelsTotalEmissions.builder()
                .fuelType(fuelType)
                .emissionsFactor(emissionFactor)
                .fuelConsumption(fuelConsumption.setScale(3, RoundingMode.HALF_UP))
                .netCalorificValue(fuelType.getNetCalorificValue())
                .emissions(emissions.setScale(3, RoundingMode.HALF_UP))
                .build();
    }

    private AviationAerDomesticFlightsEmissionsDetails calculateDomesticFlightsEmissionsDetails(List<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                                                                AviationAerUkEtsFuelType fuelType) {

        final Integer flightsNumber = calculateFlightsNumber(aggregatedEmissionDataDetails);

        final BigDecimal totalFuelConsumption = calculateTotalFuelConsumption(aggregatedEmissionDataDetails);

        final BigDecimal emissions = calculateEmissions(totalFuelConsumption, fuelType.getEmissionFactor());

        return AviationAerDomesticFlightsEmissionsDetails.builder()
                .country(aggregatedEmissionDataDetails.get(0).getAirportFrom().getCountry())
                .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                        .flightsNumber(flightsNumber)
                        .fuelType(fuelType)
                        .fuelConsumption(totalFuelConsumption)
                        .emissions(emissions.setScale(3, RoundingMode.HALF_UP))
                        .build())
                .build();
    }

    private AviationAerNonDomesticFlightsEmissionsDetails calculateNonDomesticFlightsEmissionsDetails(List<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                                                                      AviationAerDepartureArrivalStateFuelUsedTriplet triplet) {

        final Integer flightsNumber = calculateFlightsNumber(aggregatedEmissionDataDetails);

        final BigDecimal totalFuelConsumption = calculateTotalFuelConsumption(aggregatedEmissionDataDetails);

        final BigDecimal emissions = calculateEmissions(totalFuelConsumption, triplet.getFuelType().getEmissionFactor());


        return AviationAerNonDomesticFlightsEmissionsDetails.builder()
                .departureCountry(triplet.getDepartureCountry())
                .arrivalCountry(triplet.getArrivalCountry())
                .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                        .fuelType(triplet.getFuelType())
                        .flightsNumber(flightsNumber)
                        .fuelConsumption(totalFuelConsumption)
                        .emissions(emissions.setScale(3, RoundingMode.HALF_UP))
                        .build())
                .build();
    }

    private Integer calculateFlightsNumber(List<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getFlightsNumber)
                .reduce(0, Integer::sum);
    }

    private BigDecimal calculateTotalFuelConsumption(List<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getFuelConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalFlightsEmissions(List<AviationAerFlightsEmissionsDetails> flightsEmissionsDetails) {

        return flightsEmissionsDetails.stream()
                .map(AviationAerFlightsEmissionsDetails::getEmissions)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateEmissions(AviationAerUkEtsAggregatedEmissionDataDetails aggregatedEmissionDataDetails) {
      return calculateEmissions(aggregatedEmissionDataDetails.getFuelConsumption(),
              aggregatedEmissionDataDetails.getFuelType().getEmissionFactor());
    }

    private BigDecimal calculateEmissions(BigDecimal fuelConsumption, BigDecimal emissionsFactor) {
        return fuelConsumption.multiply(emissionsFactor);
    }
}
