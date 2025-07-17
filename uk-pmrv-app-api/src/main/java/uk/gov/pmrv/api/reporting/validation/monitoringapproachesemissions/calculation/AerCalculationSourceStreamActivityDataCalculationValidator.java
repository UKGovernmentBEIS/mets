package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataAggregationMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AerCalculationSourceStreamActivityDataCalculationValidator implements AerCalculationSourceStreamEmissionValidator {
    private final List<AerCalculationSourceStreamActivityDataConditionCalculation> aerCalculationSourceStreamActivityDataConditionCalculations;

    @Override
    public List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer) {
        CalculationActivityDataCalculationMethod activityDataCalculationMethod =
                sourceStreamEmission.getParameterCalculationMethod().getCalculationActivityDataCalculationMethod();

        List<AerViolation> violationsTotalMaterial = validateTotalMaterial(sourceStreamEmission, activityDataCalculationMethod);

        List<AerViolation> violationsActivityData = aerCalculationSourceStreamActivityDataConditionCalculations.stream()
                .filter(validator -> validator.getTypes().contains(sourceStreamEmission.getParameterCalculationMethod().getType()))
                .findAny()
                .map(validator -> validator.validateActivityData(sourceStreamEmission))
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        return Stream.concat(violationsTotalMaterial.stream(), violationsActivityData.stream()).toList();
    }


    private List<AerViolation> validateTotalMaterial(CalculationSourceStreamEmission sourceStreamEmission, CalculationActivityDataCalculationMethod activityDataCalculationMethod) {
        List<AerViolation> violations = new ArrayList<>();
        if(CalculationActivityDataCalculationMethodType.AGGREGATION_OF_METERING_QUANTITIES.equals(activityDataCalculationMethod.getType())) {
            CalculationActivityDataAggregationMeteringCalcMethod aggregationMeteringActivityDataCalcMethod =
                    (CalculationActivityDataAggregationMeteringCalcMethod) activityDataCalculationMethod;

            BigDecimal openingQuantity = aggregationMeteringActivityDataCalcMethod.getMaterialOpeningQuantity();
            BigDecimal closingQuantity = aggregationMeteringActivityDataCalcMethod.getMaterialClosingQuantity();

            BigDecimal totalMaterial = openingQuantity.subtract(closingQuantity);

            if(Boolean.TRUE.equals(aggregationMeteringActivityDataCalcMethod.getMaterialImportedOrExported())) {
                BigDecimal importedQuantity = aggregationMeteringActivityDataCalcMethod.getMaterialImportedQuantity();
                BigDecimal exportedQuantity = aggregationMeteringActivityDataCalcMethod.getMaterialExportedQuantity();

                totalMaterial = totalMaterial.add(importedQuantity.subtract(exportedQuantity));
            }

            BigDecimal activityDataCalcMethodTotalMaterial = aggregationMeteringActivityDataCalcMethod.getTotalMaterial();

            if(activityDataCalcMethodTotalMaterial.compareTo(totalMaterial) != 0) {
                violations.add(
                        new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_MATERIAL,
                                sourceStreamEmission.getSourceStream()));
            }
        }
        return violations;
    }
}
