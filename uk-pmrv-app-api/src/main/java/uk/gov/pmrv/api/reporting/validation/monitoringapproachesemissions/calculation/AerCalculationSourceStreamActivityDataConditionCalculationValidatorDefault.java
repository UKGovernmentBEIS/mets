package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerCalculationSourceStreamActivityDataConditionCalculationValidatorDefault implements AerCalculationSourceStreamActivityDataConditionCalculation {

    @Override
    public List<AerViolation> validateActivityData(CalculationSourceStreamEmission sourceStreamEmission) {
        List<AerViolation> violations = new ArrayList<>();

        if (sourceStreamEmission.getParameterCalculationMethod().getCalculationActivityDataCalculationMethod().getActivityData()
                .compareTo(sourceStreamEmission.getParameterCalculationMethod().getCalculationActivityDataCalculationMethod().getTotalMaterial()) != 0) {
            violations.add(
                    new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                            AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA,
                            sourceStreamEmission.getSourceStream()));
        }
        return violations;
    }

    @Override
    public List<CalculationParameterCalculationMethodType> getTypes() {
        return List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA);
    }
}
