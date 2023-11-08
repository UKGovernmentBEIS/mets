package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.List;

public interface AerCalculationSourceStreamActivityDataConditionCalculation {
    List<CalculationParameterCalculationMethodType> getTypes();
    List<AerViolation> validateActivityData(CalculationSourceStreamEmission sourceStreamEmission);

}
