package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.AerMeasurementEmissionPointEmissionValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerMeasurementCO2EmissionsValidator implements AerContextValidator {

    private final List<AerMeasurementEmissionPointEmissionValidator> commonMeasurementSourceStreamEmissionValidators;
    private final List<AerMeasurementCO2EmissionPointEmissionValidator> measurementCO2SourceStreamEmissionValidators;

    @Override
    public AerValidationResult validate(@Valid AerContainer aerContainer) {

        MonitoringApproachType type = MonitoringApproachType.MEASUREMENT_CO2;
        final Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions =
            aerContainer.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions();

        List<AerViolation> aerViolations = new ArrayList<>();
        if (monitoringApproachEmissions.containsKey(type)) {
            final MeasurementOfCO2Emissions measurementEmissions =
                (MeasurementOfCO2Emissions) monitoringApproachEmissions.get(type);
            measurementEmissions.getEmissionPointEmissions().forEach(
                measurementSourceStreamEmission -> {
                    List<AerViolation> violations = commonMeasurementSourceStreamEmissionValidators.stream()
                        .map(validator -> validator.validate(measurementSourceStreamEmission, aerContainer))
                        .flatMap(Collection::stream).toList();
                    aerViolations.addAll(violations);

                    violations = measurementCO2SourceStreamEmissionValidators.stream()
                        .map(validator -> validator.validate(measurementSourceStreamEmission, aerContainer))
                        .flatMap(Collection::stream).toList();
                    aerViolations.addAll(violations);
                }
            );
        }

        return AerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }
}
