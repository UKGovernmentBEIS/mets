package uk.gov.pmrv.api.aviationreporting.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaAerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaDepartureArrivalStateFuelUsedTriplet;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaStandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaSubmittedEmissionsCalculationService implements AviationAerSubmittedEmissionsCalculationService<AviationAerCorsiaContainer, AviationAerCorsiaSubmittedEmissions> {

    private final AviationRptAirportsService airportsService;

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }

    @Override
    public AviationAerCorsiaSubmittedEmissions calculateSubmittedEmissions(AviationAerCorsiaContainer aerContainer) {
        AviationAerCorsia aer = aerContainer.getAer();
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO emissionsCalculationDTO = AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(aer.getAggregatedEmissionsData())
                .emissionsReductionClaim(aer.getEmissionsReductionClaim().getEmissionsReductionClaimDetails() != null ?
                        aer.getEmissionsReductionClaim().getEmissionsReductionClaimDetails().getTotalEmissions() :
                        BigDecimal.ZERO)
                .year(aerContainer.getReportingYear())
                .build();
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO flightsEmissionsCalculationDTO =
                AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO.builder()
                        .aggregatedEmissionsData(emissionsCalculationDTO.getAggregatedEmissionsData())
                        .emissionsReductionClaim(emissionsCalculationDTO.getEmissionsReductionClaim())
                        .year(aerContainer.getReportingYear())
                        .build();

        return AviationAerCorsiaSubmittedEmissions.builder()
                .totalEmissions(calculateTotalSubmittedEmissions(aerContainer.getAer(), aerContainer.getReportingYear()))
                .aerodromePairsTotalEmissions(calculateAerodromePairsTotalEmissions(emissionsCalculationDTO))
                .standardFuelsTotalEmissions(calculateStandardFuelsTotalEmissions(emissionsCalculationDTO))
                .flightsEmissions(calculateInternationalFlightsEmissions(flightsEmissionsCalculationDTO))
                .build();
    }

    public AviationAerCorsiaTotalEmissions calculateTotalSubmittedEmissions(AviationAerCorsia aer, Year yearOfReport) {
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO calculationDTO = AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO.builder()
                .year(yearOfReport)
                .aggregatedEmissionsData(aer.getAggregatedEmissionsData())
            .emissionsReductionClaim(aer.getEmissionsReductionClaim().getEmissionsReductionClaimDetails() != null ?
                aer.getEmissionsReductionClaim().getEmissionsReductionClaimDetails().getTotalEmissions() :
                BigDecimal.ZERO)
            .build();

        return calculateTotalEmissions(calculationDTO);
    }

    public AviationAerCorsiaTotalEmissions calculateTotalEmissions(AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO
                                                                       emissionsCalculationDTO) {

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
            emissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final Integer totalFlights = calculateTotalFlights(aggregatedEmissionDataDetails);
        final BigDecimal totalEmissions = calculateTotalEmissions(aggregatedEmissionDataDetails);

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> offsettingAggregatedEmissionDataDetails =
            findOffsettingFlights(aggregatedEmissionDataDetails, emissionsCalculationDTO.getYear());
        final Integer offsettingTotalFlights = calculateTotalFlights(offsettingAggregatedEmissionDataDetails);
        final BigDecimal offsettingTotalEmissions = calculateTotalEmissions(offsettingAggregatedEmissionDataDetails);

        Set<AviationAerCorsiaAggregatedEmissionDataDetails> nonOffsettingAggregatedEmissionDataDetails =
            new HashSet<>(aggregatedEmissionDataDetails);
        nonOffsettingAggregatedEmissionDataDetails.removeAll(offsettingAggregatedEmissionDataDetails);
        final Integer nonOffsettingTotalFlights = calculateTotalFlights(nonOffsettingAggregatedEmissionDataDetails);
        final BigDecimal nonOffsettingTotalEmissions = calculateTotalEmissions(nonOffsettingAggregatedEmissionDataDetails);

        return AviationAerCorsiaTotalEmissions.builder()
            .allFlightsNumber(totalFlights)
            .allFlightsEmissions(totalEmissions)
            .offsetFlightsNumber(offsettingTotalFlights)
            .offsetFlightsEmissions(offsettingTotalEmissions)
            .nonOffsetFlightsNumber(nonOffsettingTotalFlights)
            .nonOffsetFlightsEmissions(nonOffsettingTotalEmissions)
            .emissionsReductionClaim(emissionsCalculationDTO.getEmissionsReductionClaim() != null ?
                emissionsCalculationDTO.getEmissionsReductionClaim() : BigDecimal.ZERO)
            .build();

    }

    public List<AviationAerCorsiaAerodromePairsTotalEmissions> calculateAerodromePairsTotalEmissions(AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO
                                                                                                         emissionsCalculationDTO) {

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails
            = emissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        List<AviationAerCorsiaAerodromePairsTotalEmissions> aerodromePairsTotalEmissions = new ArrayList<>();

        final Set<String> icaoCodes = getIcaoCodes(aggregatedEmissionDataDetails);
        final List<String> chapter3IcaoCodes = airportsService.findChapter3Icaos(icaoCodes, emissionsCalculationDTO.getYear());

        final Map<AviationRptAirportsDTO, Map<AviationRptAirportsDTO, Set<AviationAerCorsiaAggregatedEmissionDataDetails>>> aggregatedEmissionsPerAirportPair
            = aggregatedEmissionDataDetails.stream()
            .collect(Collectors.groupingBy(AviationAerAggregatedEmissionDataDetails::getAirportFrom,
                Collectors.groupingBy(AviationAerAggregatedEmissionDataDetails::getAirportTo, Collectors.toSet())));

        aggregatedEmissionsPerAirportPair.forEach((airportFrom, emissionsDataPerArrivalAirport) ->
            emissionsDataPerArrivalAirport.forEach((airportTo, emissionData) ->
                aerodromePairsTotalEmissions.add(AviationAerCorsiaAerodromePairsTotalEmissions.builder()
                    .departureAirport(airportFrom)
                    .arrivalAirport(airportTo)
                    .flightsNumber(calculateTotalFlights(emissionData))
                    .emissions(emissionData.stream()
                        .map(this::calculateEmissions)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(3, RoundingMode.HALF_UP))
                    .offset(isOffset(airportFrom.getIcao(), airportTo.getIcao(), chapter3IcaoCodes))
                    .build())
            ));
        return aerodromePairsTotalEmissions;
    }

    private Integer calculateTotalFlights(Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getFlightsNumber)
            .reduce(0, Integer::sum);
    }

    private BigDecimal calculateTotalEmissions(Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
            .map(this::calculateEmissions)
            .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateEmissions(AviationAerCorsiaAggregatedEmissionDataDetails aggregatedEmissionDataDetails) {
        final BigDecimal fuelConsumption = aggregatedEmissionDataDetails.getFuelConsumption();
        final BigDecimal emissionFactor = aggregatedEmissionDataDetails.getFuelType().getEmissionFactor();
        return fuelConsumption.multiply(emissionFactor);
    }

    public Set<AviationAerCorsiaAggregatedEmissionDataDetails> findOffsettingFlights(Set<AviationAerCorsiaAggregatedEmissionDataDetails>
                                                                                         aggregatedEmissionDataDetails, Year yearOfReport) {
        final Set<String> icaoCodes = getIcaoCodes(aggregatedEmissionDataDetails);
        final List<String> chapter3IcaoCodes = airportsService.findChapter3Icaos(icaoCodes, yearOfReport);
        return aggregatedEmissionDataDetails.stream()
            .filter(details -> isOffset(details, chapter3IcaoCodes))
            .collect(Collectors.toSet());
    }

    private Set<String> getIcaoCodes(Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        Set<String> icaoCodes = aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getAirportFrom)
            .map(AviationRptAirportsDTO::getIcao)
            .collect(Collectors.toSet());

        Set<String> icaoCodesAirportTo = aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getAirportTo)
            .map(AviationRptAirportsDTO::getIcao)
            .collect(Collectors.toSet());

        icaoCodes.addAll(icaoCodesAirportTo);
        return icaoCodes;
    }

    public List<AviationAerCorsiaStandardFuelsTotalEmissions> calculateStandardFuelsTotalEmissions(AviationAerCorsiaEmissionsCalculationDTO
                                                                                                       emissionsCalculationDTO) {

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
            emissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        List<AviationAerCorsiaStandardFuelsTotalEmissions> standardFuelsTotalEmissions = new ArrayList<>();

        final Map<AviationAerCorsiaFuelType, List<AviationAerCorsiaAggregatedEmissionDataDetails>> aggregatedDataPerFuelType = aggregatedEmissionDataDetails.stream()
            .collect(Collectors.groupingBy(AviationAerCorsiaAggregatedEmissionDataDetails::getFuelType));

        aggregatedDataPerFuelType.keySet().forEach(fuelType ->
            standardFuelsTotalEmissions.add(calculateStandardFuelTotalEmissions(aggregatedDataPerFuelType.get(fuelType))));
        return standardFuelsTotalEmissions;
    }

    private AviationAerCorsiaStandardFuelsTotalEmissions calculateStandardFuelTotalEmissions(List<AviationAerCorsiaAggregatedEmissionDataDetails>
                                                                                                 aggregatedEmissionDataDetails) {

        final BigDecimal fuelConsumption = aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getFuelConsumption)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final AviationAerCorsiaFuelType fuelType = aggregatedEmissionDataDetails.get(0).getFuelType();
        final BigDecimal emissionsFactor = fuelType.getEmissionFactor();
        final BigDecimal emissions = fuelConsumption.multiply(emissionsFactor);
        return AviationAerCorsiaStandardFuelsTotalEmissions.builder()
            .fuelType(fuelType)
            .emissionsFactor(emissionsFactor)
            .fuelConsumption(fuelConsumption.setScale(3, RoundingMode.HALF_UP))
            .emissions(emissions.setScale(3, RoundingMode.HALF_UP))
            .build();
    }

    public AviationAerCorsiaInternationalFlightsEmissions calculateInternationalFlightsEmissions(AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
            aviationAerEmissionsCalculationDTO.getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final Set<String> icaoCodes = getIcaoCodes(aggregatedEmissionDataDetails);
        final List<String> chapter3IcaoCodes = airportsService.findChapter3Icaos(icaoCodes, aviationAerEmissionsCalculationDTO.getYear());

        final Map<AviationAerCorsiaDepartureArrivalStateFuelUsedTriplet, List<AviationAerCorsiaAggregatedEmissionDataDetails>> corsiaDepartureArrivalStateFuelUsedTripletMap = aggregatedEmissionDataDetails.stream()
            .collect(Collectors.groupingBy(details -> AviationAerCorsiaDepartureArrivalStateFuelUsedTriplet.builder()
                .departureState(details.getAirportFrom().getState())
                .arrivalState(details.getAirportTo().getState())
                .fuelType(details.getFuelType())
                .build()));

        final List<AviationAerCorsiaInternationalFlightsEmissionsDetails> corsiaFlightsEmissionsDetails = corsiaDepartureArrivalStateFuelUsedTripletMap
            .entrySet().stream()
            .map(entry -> calculateFlightsEmissionsDetails(entry.getValue(), chapter3IcaoCodes, entry.getKey()))
            .collect(Collectors.toList());

        return AviationAerCorsiaInternationalFlightsEmissions.builder()
            .flightsEmissionsDetails(corsiaFlightsEmissionsDetails)
            .build();
    }

    private AviationAerCorsiaInternationalFlightsEmissionsDetails calculateFlightsEmissionsDetails(
        List<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails, List<String> chapter3IcaoCodes,
        AviationAerCorsiaDepartureArrivalStateFuelUsedTriplet triplet) {

        final BigDecimal fuelConsumption = aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getFuelConsumption)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal emissions = fuelConsumption.multiply(triplet.getFuelType().getEmissionFactor()).setScale(3, RoundingMode.HALF_UP);
        final Integer flightsNumber = aggregatedEmissionDataDetails.stream()
            .map(AviationAerAggregatedEmissionDataDetails::getFlightsNumber)
            .reduce(0, Integer::sum);

        return AviationAerCorsiaInternationalFlightsEmissionsDetails.builder()
            .departureState(triplet.getDepartureState())
            .arrivalState(triplet.getArrivalState())
            .flightsEmissionsDetails(AviationAerCorsiaFlightsEmissionsDetails.builder()
                .fuelType(triplet.getFuelType())
                .flightsNumber(flightsNumber)
                .fuelConsumption(fuelConsumption)
                .emissions(emissions)
                .offset(isOffset(aggregatedEmissionDataDetails, chapter3IcaoCodes))
                .build())
            .build();
    }

    private boolean isOffset(List<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails, List<String> chapter3IcaoCodes) {

        return aggregatedEmissionDataDetails.stream().anyMatch(details -> isOffset(details, chapter3IcaoCodes));
    }

    private static boolean isOffset(AviationAerCorsiaAggregatedEmissionDataDetails aggregatedEmissionDataDetails, List<String> chapter3IcaoCodes) {
        return chapter3IcaoCodes.contains(aggregatedEmissionDataDetails.getAirportFrom().getIcao())
            && chapter3IcaoCodes.contains(aggregatedEmissionDataDetails.getAirportTo().getIcao());
    }

    private static boolean isOffset(String departureIcao, String arrivalIcao, List<String> chapter3IcaoCodes) {
        return chapter3IcaoCodes.contains(departureIcao) && chapter3IcaoCodes.contains(arrivalIcao);
    }
}
