package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerCalculationSourceStreamBiomassMonitoringTierValidator implements AerCalculationSourceStreamEmissionValidator {

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        boolean sourceStreamContainsBiomass = sourceStreamEmission.getBiomassPercentages().getContains();
        boolean biomassFractionMonitoringTierDefined = sourceStreamEmission.getParameterMonitoringTiers().stream()
            .anyMatch(paramMonitoringTier -> paramMonitoringTier.getType().equals(CalculationParameterType.BIOMASS_FRACTION));

        if(sourceStreamContainsBiomass ^ biomassFractionMonitoringTierDefined) {
            violations.add(
                new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                    AerViolation.AerViolationMessage.CALCULATION_INVALID_BIOMASS_FRACTION_MONITORING_TIER,
                    sourceStreamEmission.getSourceStream()));
        }

        return violations;
    }
}
