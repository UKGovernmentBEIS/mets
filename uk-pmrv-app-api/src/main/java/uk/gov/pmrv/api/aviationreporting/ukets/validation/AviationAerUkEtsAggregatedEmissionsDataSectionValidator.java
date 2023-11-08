package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerAggregatedEmissionsDataSectionCommonValidator;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsAggregatedEmissionsDataSectionValidator implements AviationAerUkEtsContextValidator {

    private final AviationAerAggregatedEmissionsDataSectionCommonValidator commonValidator;

    @Override
    public AviationAerValidationResult validate(AviationAerUkEtsContainer aerContainer) {

        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aerContainer.getAer().getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final AviationAerValidationResult commonResult = commonValidator.validate(aggregatedEmissionDataDetails, aerContainer.getReportingYear());
        if (!commonResult.isValid()) {
            return commonResult;
        }

        final List<AviationAerViolation> aerViolations = new ArrayList<>(commonResult.getAerViolations());

        final List<AviationAerUkEtsAggregatedEmissionDataDetails> invalidDepartureAirportCountryTypeData = checkDepartureAirportsCountryTypes(aggregatedEmissionDataDetails);
        if (!invalidDepartureAirportCountryTypeData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerUkEtsAggregatedEmissionsData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_AIRPORT_COUNTRY_TYPE, invalidDepartureAirportCountryTypeData.toArray()));
        }

        final List<AviationAerUkEtsAggregatedEmissionDataDetails> invalidArrivalAirportsForEEAReportedDepartureData =
                checkArrivalAirportsForEEAReportedDepartureAirports(aggregatedEmissionDataDetails);
        if (!invalidArrivalAirportsForEEAReportedDepartureData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerUkEtsAggregatedEmissionsData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_REPORTED_DEPARTURE, invalidArrivalAirportsForEEAReportedDepartureData.toArray()));
        }

        final List<AviationAerUkEtsAggregatedEmissionDataDetails> invalidArrivalAirportsForEEANotReportedDepartureData
                = checkArrivalAirportsForEEANotReportedDepartureAirports(aggregatedEmissionDataDetails);
        if (!invalidArrivalAirportsForEEANotReportedDepartureData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerUkEtsAggregatedEmissionsData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_NOT_REPORTED_DEPARTURE, invalidArrivalAirportsForEEANotReportedDepartureData.toArray()));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private List<AviationAerUkEtsAggregatedEmissionDataDetails> checkDepartureAirportsCountryTypes(Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
                .filter(aerAggregatedEmissionDataDetail -> !CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(aerAggregatedEmissionDataDetail.getAirportFrom().getCountryType()) &&
                        !CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED.equals(aerAggregatedEmissionDataDetail.getAirportFrom().getCountryType()))
                .toList();
    }

    private List<AviationAerUkEtsAggregatedEmissionDataDetails> checkArrivalAirportsForEEAReportedDepartureAirports(Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
                .filter(details -> CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(details.getAirportFrom().getCountryType()) &&
                        (!CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(details.getAirportTo().getCountryType()) &&
                                !CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED.equals(details.getAirportTo().getCountryType()) &&
                                !CountryType.EEA_COUNTRY.equals(details.getAirportTo().getCountryType()) &&
                                !CountryType.EFTA_COUNTRY.equals(details.getAirportTo().getCountryType())))
                .toList();
    }

    private List<AviationAerUkEtsAggregatedEmissionDataDetails> checkArrivalAirportsForEEANotReportedDepartureAirports(Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails) {
        return aggregatedEmissionDataDetails.stream()
                .filter(details -> CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED.equals(details.getAirportFrom().getCountryType()) &&
                        (!CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED.equals(details.getAirportTo().getCountryType())))
                .toList();
    }
}
