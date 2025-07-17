package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.transform.CalculationParameterMeasurementUnitsMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public abstract class SourceStreamEmissionsCalculationService implements SourceStreamCategoryEmissionsCalculationService {

    private final CalculationParameterMeasurementUnitsMapper calculationEmissionsMeasurementUnitsMapper =
        Mappers.getMapper(CalculationParameterMeasurementUnitsMapper.class);

    protected abstract BigDecimal getCalculationFactor(EmissionsCalculationParamsDTO calculationParams);

    @Override
    public EmissionsCalculationDTO calculateEmissions(EmissionsCalculationParamsDTO calculationParams) {
        validateParamsMeasurementUnits(calculationParams);

        Pair<BigDecimal, BigDecimal> standardEmissions = getStandardEmissionCalculationFormulaResults(calculationParams);
        BigDecimal extraCalcFactor = getCalculationFactor(calculationParams);

        BigDecimal reportableEmissions = standardEmissions.getLeft().multiply(extraCalcFactor);
        BigDecimal sustainableBiomassEmissions = standardEmissions.getRight() != null ? standardEmissions.getRight().multiply(extraCalcFactor) : null;

        return EmissionsCalculationDTO.builder()
            .reportableEmissions(reportableEmissions.setScale(5, RoundingMode.HALF_UP))
            .sustainableBiomassEmissions(sustainableBiomassEmissions != null ? sustainableBiomassEmissions.setScale(5, RoundingMode.HALF_UP) : null)
            .build();
    }

    private void validateParamsMeasurementUnits(EmissionsCalculationParamsDTO calculationParams) {
        CalculationParameterMeasurementUnits measurementUnits =
            calculationEmissionsMeasurementUnitsMapper.toCalculationParameterMeasurementUnits(calculationParams);

        if(!getValidMeasurementUnitsCombinations().contains(measurementUnits)) {
            throw new BusinessException(MetsErrorCode.AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION);
        }
    }

    private Pair<BigDecimal, BigDecimal> getStandardEmissionCalculationFormulaResults(EmissionsCalculationParamsDTO calculationParams) {
        BigDecimal activityData = calculationParams.getActivityData();
        BigDecimal biomassPercentage = calculationParams.getBiomassPercentage();

        if(calculationParams.isContainsBiomass()) {
            BigDecimal biomass = biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP);
            BigDecimal reportableEmissions = activityData.multiply(BigDecimal.ONE.subtract(biomass));
            BigDecimal sustainableBiomassEmissions = activityData.multiply(biomass);
            return Pair.of(reportableEmissions, sustainableBiomassEmissions);
        }

        return Pair.of(activityData, null);
    }
}
