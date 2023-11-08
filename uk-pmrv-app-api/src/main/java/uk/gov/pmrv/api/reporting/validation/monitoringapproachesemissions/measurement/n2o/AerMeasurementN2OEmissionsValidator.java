package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.AerMeasurementEmissionPointEmissionValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerMeasurementN2OEmissionsValidator implements AerContextValidator {

    private final List<AerMeasurementEmissionPointEmissionValidator> commonMeasurementEmissionPointEmissionValidators;
    private final List<AerMeasurementN2OEmissionPointEmissionValidator> measurementEmissionPointEmissionValidators;

    @Override
    public AerValidationResult validate(@Valid AerContainer aerContainer) {

        MonitoringApproachType type = MonitoringApproachType.MEASUREMENT_N2O;
        final Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions =
            aerContainer.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions();

        List<AerViolation> aerViolations = new ArrayList<>();
        if (monitoringApproachEmissions.containsKey(type)) {
            final MeasurementOfN2OEmissions measurementEmissions =
                (MeasurementOfN2OEmissions) monitoringApproachEmissions.get(type);
            measurementEmissions.getEmissionPointEmissions().forEach(
                measurementEmissionPointEmission -> {
                    List<AerViolation> violations = measurementEmissionPointEmissionValidators.stream()
                        .map(validator -> validator.validate(measurementEmissionPointEmission, aerContainer))
                        .flatMap(Collection::stream).toList();
                    aerViolations.addAll(violations);

                    violations = commonMeasurementEmissionPointEmissionValidators.stream()
                        .map(validator -> validator.validate(measurementEmissionPointEmission, aerContainer))
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
