package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerCalculationSourceStreamParamCalculationMethodValidator implements AerCalculationSourceStreamEmissionValidator {

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        List<CalculationParameterMonitoringTier> parameterMonitoringTiers = sourceStreamEmission.getParameterMonitoringTiers();
        CalculationParameterCalculationMethod parameterCalculationMethod = sourceStreamEmission.getParameterCalculationMethod();
        CalculationParameterCalculationMethodType parameterCalculationMethodType = parameterCalculationMethod.getType();

        CalculationNetCalorificValueTier ncvMonitoringTier = parameterMonitoringTiers.stream()
            .filter(tier -> tier.getType().equals(CalculationParameterType.NET_CALORIFIC_VALUE))
            .findFirst()
            .map(CalculationNetCalorificValueMonitoringTier.class::cast)
            .map(CalculationNetCalorificValueMonitoringTier::getTier)
            .orElse(null);

        CalculationEmissionFactorTier emissionFactorMonitoringTier = parameterMonitoringTiers.stream()
            .filter(tier -> tier.getType().equals(CalculationParameterType.EMISSION_FACTOR))
            .findFirst()
            .map(CalculationEmissionFactorMonitoringTier.class::cast)
            .map(CalculationEmissionFactorMonitoringTier::getTier)
            .orElse(null);

        boolean oxidationFactorMonitoringTierDefined = sourceStreamEmission.getParameterMonitoringTiers().stream()
            .anyMatch(paramMonitoringTier -> paramMonitoringTier.getType().equals(CalculationParameterType.OXIDATION_FACTOR));

        if((ncvMonitoringTier != CalculationNetCalorificValueTier.TIER_2A ||
            (emissionFactorMonitoringTier != CalculationEmissionFactorTier.TIER_2 && emissionFactorMonitoringTier != CalculationEmissionFactorTier.TIER_2A)
            || !oxidationFactorMonitoringTierDefined) &&
            parameterCalculationMethodType != CalculationParameterCalculationMethodType.MANUAL) {

                violations.add(
                    new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                        AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_CALCULATION_METHOD,
                        sourceStreamEmission.getSourceStream())
                );
        }


        return violations;
    }
}
