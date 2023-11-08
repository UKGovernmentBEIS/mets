package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerFallbackEmissionsValidator implements AerContextValidator {

    private final List<AerFallbackEmissionValidator> validators;

    @Override
    public AerValidationResult validate(@Valid AerContainer aerContainer) {

        MonitoringApproachType type = MonitoringApproachType.FALLBACK;
        final Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions =
            aerContainer.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions();

        List<AerViolation> aerViolations = monitoringApproachEmissions.containsKey(type) ?
            validators.stream()
                .map(validator ->
                    validator.validate(
                        ((FallbackEmissions) monitoringApproachEmissions.get(type)), aerContainer
                    )
                )
                .flatMap(Collection::stream).toList() :
            new ArrayList<>();

        return AerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }
}
