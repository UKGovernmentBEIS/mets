package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaAircraftTypeDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFlightType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaEmissionsMonitoringApproachSectionValidator implements AviationAerCorsiaContextValidator {

    private final AircraftTypeQueryService aircraftTypeQueryService;

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaContainer aerContainer) {

        final List<AviationAerViolation> aerViolations = new ArrayList<>();

        final AviationAerCorsiaMonitoringApproach monitoringApproach =
                aerContainer.getAer().getMonitoringApproach();

        if (monitoringApproach.getCertDetails() != null && ((AviationAerCorsiaFlightType.ALL_INTERNATIONAL_FLIGHTS.equals(monitoringApproach.getCertDetails().getFlightType())
                && monitoringApproach.getFuelUseMonitoringDetails() != null) ||
                (!AviationAerCorsiaFlightType.ALL_INTERNATIONAL_FLIGHTS.equals(monitoringApproach.getCertDetails().getFlightType())
                        && monitoringApproach.getFuelUseMonitoringDetails() == null))) {
            aerViolations.add(new AviationAerViolation(
                    AviationAerCorsiaMonitoringApproach.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION));
        }

        if (monitoringApproach.getFuelUseMonitoringDetails() != null &&
                !monitoringApproach.getFuelUseMonitoringDetails().getAircraftTypeDetails().isEmpty()) {
            final Set<String> designators = monitoringApproach.getFuelUseMonitoringDetails().getAircraftTypeDetails().stream()
                    .map(AviationAerCorsiaAircraftTypeDetails::getDesignator).collect(Collectors.toSet());
            final List<String> invalidDesignators = aircraftTypeQueryService.findInvalidDesignatorCodes(new ArrayList<>(designators));
            if (!invalidDesignators.isEmpty()) {
                List<AviationAerCorsiaAircraftTypeDetails> invalidData = new ArrayList<>();
                invalidDesignators.forEach(
                        designator -> invalidData.addAll(monitoringApproach.getFuelUseMonitoringDetails().getAircraftTypeDetails()
                                .stream().filter(data -> designator.equals(data.getDesignator())).toList())
                );

                aerViolations.add(new AviationAerViolation(AviationAerCorsiaAircraftTypeDetails.class.getSimpleName(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_AIRCRAFT_DATA_DESIGNATOR, invalidData.toArray()));
            }
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }
}
