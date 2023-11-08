package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationFuelMeteringConditionType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationRegionalDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AerCalculationSourceStreamActivityDataConditionCalculationValidatorRegional implements AerCalculationSourceStreamActivityDataConditionCalculation {

    @Override
    public List<AerViolation> validateActivityData(CalculationSourceStreamEmission sourceStreamEmission) {
        List<AerViolation> violations = new ArrayList<>();

        CalculationRegionalDataCalculationMethod parameterCalculationMethod = (CalculationRegionalDataCalculationMethod) sourceStreamEmission.getParameterCalculationMethod();
        if (parameterCalculationMethod.getFuelMeteringConditionType() == CalculationFuelMeteringConditionType.CELSIUS_15) {
            BigDecimal activityData = parameterCalculationMethod.getCalculationActivityDataCalculationMethod().getTotalMaterial()
                    .multiply(parameterCalculationMethod.getEmissionCalculationParamValues().getCalculationFactor());

            if (activityData.compareTo(parameterCalculationMethod.getCalculationActivityDataCalculationMethod().getActivityData()) != 0) {
                violations.add(
                        new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA,
                                sourceStreamEmission.getSourceStream()));
            }
            return violations;
        } else {
            if (sourceStreamEmission.getParameterCalculationMethod().getCalculationActivityDataCalculationMethod().getActivityData()
                    .compareTo(sourceStreamEmission.getParameterCalculationMethod().getCalculationActivityDataCalculationMethod().getTotalMaterial()) != 0) {
                violations.add(
                        new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA,
                                sourceStreamEmission.getSourceStream()));
            }
            return violations;
        }
    }

    @Override
    public List<CalculationParameterCalculationMethodType> getTypes() {
        return List.of(CalculationParameterCalculationMethodType.REGIONAL_DATA);
    }
}
