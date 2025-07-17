package uk.gov.pmrv.api.aviationreporting.common.validation;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElementsUtils;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElementsUtilsEqualResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;



@Service
@RequiredArgsConstructor
public class AviationAerAggregatedEmissionsDataSectionCommonValidator {

    private final AviationRptAirportsService airportsService;
    
    public AviationAerValidationResult validate(final Set<? extends AviationAerAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                final Year reportingYear) {

        final List<AviationAerViolation> aerViolations = new ArrayList<>();

        final Set<String> icaoCodes = extractIcaoCodes(aggregatedEmissionDataDetails);
        final List<AviationRptAirportsDTO> existingAirports =
            airportsService.getAirportsByIcaoCodesAndYear(new HashSet<>(icaoCodes), reportingYear);

        final List<? extends AviationAerAggregatedEmissionDataDetails> invalidIcaoData =
            this.checkAirportIcaoExistence(aggregatedEmissionDataDetails, existingAirports);

        if (!invalidIcaoData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerAggregatedEmissionDataDetails.class.getSimpleName(),
                AviationAerViolation.AviationAerViolationMessage.INVALID_ICAO_CODE, invalidIcaoData.toArray()));
            return AviationAerValidationResult.builder()
                .valid(false)
                .aerViolations(aerViolations)
                .build();
        }

        final List<? extends AviationAerAggregatedEmissionDataDetails> invalidCountryTypeData =
            this.checkAirportCountryTypesValidity(aggregatedEmissionDataDetails, existingAirports);
        if (!invalidCountryTypeData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerAggregatedEmissionDataDetails.class.getSimpleName(),
                AviationAerViolation.AviationAerViolationMessage.INVALID_COUNTRY_TYPE,
                invalidCountryTypeData.toArray()));
            return AviationAerValidationResult.builder()
                .valid(false)
                .aerViolations(aerViolations)
                .build();
        }

        final List<? extends AviationAerAggregatedEmissionDataDetails> equalAirportsDetails =
            this.checkForDepartureArrivalAirportsEquality(aggregatedEmissionDataDetails);
        if (!equalAirportsDetails.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerAggregatedEmissionDataDetails.class.getSimpleName(),
                AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_ARRIVAL_AIRPORTS,
                equalAirportsDetails.toArray()));
        }
        return AviationAerValidationResult.builder()
            .valid(true) // this is set to true so that the callers of the method continue with the validations
            .aerViolations(aerViolations)
            .build();
    }
    
    private Set<String> extractIcaoCodes(Set<? extends AviationAerAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {

        Set<String> icaoCodesAirportFrom = aggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getAirportFrom)
                .map(AviationRptAirportsDTO::getIcao)
                .collect(Collectors.toSet());

        Set<String> icaoCodesAirportTo = aggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getAirportTo)
                .map(AviationRptAirportsDTO::getIcao)
                .collect(Collectors.toSet());

        icaoCodesAirportFrom.addAll(icaoCodesAirportTo);
        return icaoCodesAirportFrom;
    }

    private List<? extends AviationAerAggregatedEmissionDataDetails> checkAirportIcaoExistence(Set<? extends AviationAerAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                                                     List<AviationRptAirportsDTO> existingAirports) {
        return aggregatedEmissionDataDetails.stream()
                .filter(aggregatedEmissionDataDetail -> {
                    try {
                        return !UniqueElementsUtils.contains(existingAirports,aggregatedEmissionDataDetail.getAirportFrom()) ||
                                !UniqueElementsUtils.contains(existingAirports,aggregatedEmissionDataDetail.getAirportTo());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    private List<? extends AviationAerAggregatedEmissionDataDetails> checkAirportCountryTypesValidity(Set<? extends AviationAerAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                                                            List<AviationRptAirportsDTO> existingAirports) {
        return aggregatedEmissionDataDetails.stream()
                .filter(aggregatedEmissionDataDetail -> !isCountryTypeValid(aggregatedEmissionDataDetail, existingAirports))
                .toList();
    }

    private boolean isCountryTypeValid(AviationAerAggregatedEmissionDataDetails aggregatedEmissionDataDetails, List<AviationRptAirportsDTO> airports) {
        
        final Optional<AviationRptAirportsDTO> airportFrom = airports.stream()
                .filter(airport -> {
                    try {
                        return UniqueElementsUtils.equal(aggregatedEmissionDataDetails.getAirportFrom(),airport).getResult()
                                && aggregatedEmissionDataDetails.getAirportFrom().getCountryType().equals(airport.getCountryType());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst();

        final Optional<AviationRptAirportsDTO> airportTo = airports.stream()
                .filter(airport -> {
                    try {
                        return UniqueElementsUtils.equal(aggregatedEmissionDataDetails.getAirportTo(),airport).getResult()
                                && aggregatedEmissionDataDetails.getAirportTo().getCountryType().equals(airport.getCountryType());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst();

        return airportFrom.isPresent() && airportTo.isPresent();
    }

    private List<? extends AviationAerAggregatedEmissionDataDetails> checkForDepartureArrivalAirportsEquality(Set<? extends AviationAerAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        
        return aggregatedEmissionDataDetails.stream().
                filter(details -> {
                    try {
                        UniqueElementsUtilsEqualResult result = UniqueElementsUtils.equal(details.getAirportFrom(),details.getAirportTo());
                        return result.getResult();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
