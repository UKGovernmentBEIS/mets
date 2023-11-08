package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerCalculationOfPfcEmissionsValidator implements AerContextValidator {

    private final List<AerCalculationOfPfcSourceStreamEmissionValidator> calculationOfPfcSourceStreamEmissionValidators;

    @Override
    public AerValidationResult validate(@Valid AerContainer aerContainer) {

        MonitoringApproachType type = MonitoringApproachType.CALCULATION_PFC;
        final Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions =
            aerContainer.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions();

        List<AerViolation> aerViolations = new ArrayList<>();
        if (monitoringApproachEmissions.containsKey(type)) {
            final CalculationOfPfcEmissions calculationOfPfcEmissions =
                (CalculationOfPfcEmissions) monitoringApproachEmissions.get(type);
            calculationOfPfcEmissions.getSourceStreamEmissions().forEach(
                sourceStreamEmission -> {
                    final List<AerViolation> violations = calculationOfPfcSourceStreamEmissionValidators.stream()
                        .map(validator -> validator.validate(sourceStreamEmission, aerContainer))
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
