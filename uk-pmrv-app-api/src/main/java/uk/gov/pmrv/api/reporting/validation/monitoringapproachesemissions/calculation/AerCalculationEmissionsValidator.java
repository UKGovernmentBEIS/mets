package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerCalculationEmissionsValidator implements AerContextValidator {

    private final List<AerCalculationSourceStreamEmissionValidator> aerCalculationSourceStreamEmissionValidators;

    @Override
    public AerValidationResult validate(@Valid AerContainer aerContainer) {
        Aer aer = aerContainer.getAer();
        MonitoringApproachType type = MonitoringApproachType.CALCULATION_CO2;
        Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions =
            aer.getMonitoringApproachEmissions().getMonitoringApproachEmissions();

        List<AerViolation> aerViolations = new ArrayList<>();

        if(monitoringApproachEmissions.containsKey(type)) {
            CalculationOfCO2Emissions calculationEmissions = (CalculationOfCO2Emissions) monitoringApproachEmissions.get(type);
            List<CalculationSourceStreamEmission> sourceStreamEmissions = calculationEmissions.getSourceStreamEmissions();

            sourceStreamEmissions.forEach(sourceStreamEmission -> {
                List<AerViolation> sourceStreamViolations = aerCalculationSourceStreamEmissionValidators.stream()
                    .map(validator -> validator.validate(sourceStreamEmission, aerContainer))
                    .flatMap(Collection::stream).toList();

                aerViolations.addAll(sourceStreamViolations);
                }
            );
        }

        return AerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }
}