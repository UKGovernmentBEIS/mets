package uk.gov.pmrv.api.aviationreporting.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.validation.AviationAerCorsiaContextValidator;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.validation.AviationAerUkEtsContextValidator;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerAircraftDataSectionValidator {

    private final AircraftTypeQueryService aircraftTypeQueryService;

    public AviationAerValidationResult validateAircaftData(Set<AviationAerAircraftDataDetails> aviationAerAircraftData, Year reportingYear) {
        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final Set<String> designators = aviationAerAircraftData.stream().map(AviationAerAircraftDataDetails::getAircraftTypeDesignator).collect(Collectors.toSet());
        final List<String> invalidDesignators = aircraftTypeQueryService.findInvalidDesignatorCodes(new ArrayList<>(designators));

        if (!invalidDesignators.isEmpty()) {
            List<AviationAerAircraftDataDetails> invalidData = new ArrayList<>();
            invalidDesignators.forEach(
                    designator -> invalidData.addAll(aviationAerAircraftData.stream().filter(data -> designator.equals(data.getAircraftTypeDesignator())).toList())
            );

            aerViolations.add(new AviationAerViolation(AviationAerAircraftData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_AIRCRAFT_DATA_DESIGNATOR, invalidData.toArray()));
        }

        final List<AviationAerAircraftDataDetails> invalidDatesData = startDateAfterEndDate(aviationAerAircraftData);
        if (!invalidDatesData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerAircraftData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_INTERVAL, invalidDatesData.toArray()));
        }

        final List<AviationAerAircraftDataDetails> invalidSchemeYearData =
                areDatesInSameSchemeYear(reportingYear.getValue(), aviationAerAircraftData);
        if (!invalidSchemeYearData.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerAircraftData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_END_DATE_SCHEME_YEAR, invalidSchemeYearData.toArray()));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private List<AviationAerAircraftDataDetails> startDateAfterEndDate(Set<AviationAerAircraftDataDetails> aircraftDataDetails) {
        return aircraftDataDetails.stream()
                .filter(data -> data.getStartDate().isAfter(data.getEndDate()))
                .collect(Collectors.toList());
    }

    private List<AviationAerAircraftDataDetails> areDatesInSameSchemeYear(Integer reportingYear, Set<AviationAerAircraftDataDetails> aircraftDataDetails) {
        return aircraftDataDetails.stream()
                .filter(data -> data.getStartDate().getYear() != reportingYear || data.getEndDate().getYear() != reportingYear)
                .collect(Collectors.toList());
    }
}
