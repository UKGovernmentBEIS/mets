package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.GlobalWarmingPotential;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.OverVoltageSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Year;

@Service
public class PfcEmissionsCalculationService {

    public PfcEmissionsCalculationDTO calculateEmissions(PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO) {
		final BigDecimal collectionEfficiency = pfcEmissionsCalculationParamsDTO
				.getPfcSourceStreamEmissionCalculationMethodData().getPercentageOfCollectionEfficiency();
        final BigDecimal totalPrimaryAluminium = pfcEmissionsCalculationParamsDTO.getTotalPrimaryAluminium(); // activity data value
		final BigDecimal c2F6WeightFraction = pfcEmissionsCalculationParamsDTO
				.getPfcSourceStreamEmissionCalculationMethodData().getC2F6WeightFraction();
        final Year reportingYear = pfcEmissionsCalculationParamsDTO.getReportingYear();

        final BigDecimal amountOfCF4;
        if (pfcEmissionsCalculationParamsDTO.getCalculationMethod() == PFCCalculationMethod.SLOPE) {
            amountOfCF4 = calculateAmountOfCF4BySlope(totalPrimaryAluminium,
                (SlopeSourceStreamEmissionCalculationMethodData) pfcEmissionsCalculationParamsDTO.getPfcSourceStreamEmissionCalculationMethodData());
        } else {
            amountOfCF4 = calculateAmountOfCF4ByOverVoltage(totalPrimaryAluminium,
                (OverVoltageSourceStreamEmissionCalculationMethodData) pfcEmissionsCalculationParamsDTO.getPfcSourceStreamEmissionCalculationMethodData());
        }

        final BigDecimal gwpCF4 = GlobalWarmingPotential.PFC_CF4.getValue(reportingYear);
        final BigDecimal gwpC2F6 = GlobalWarmingPotential.PFC_C2F6.getValue(reportingYear);
        final BigDecimal amountOfC2F6 = c2F6WeightFraction.multiply(amountOfCF4);
        final BigDecimal totalCF4Emissions = amountOfCF4.multiply(gwpCF4);
        final BigDecimal totalC2F6Emissions = amountOfC2F6.multiply(gwpC2F6);

		final BigDecimal reportableEmissions = ((totalCF4Emissions.add(totalC2F6Emissions))
				.multiply(BigDecimal.valueOf(100))).divide(collectionEfficiency, MathContext.DECIMAL128);

        return PfcEmissionsCalculationDTO.builder()
            .globalWarmingPotentialCF4(gwpCF4)
            .globalWarmingPotentialC2F6(gwpC2F6)
            .amountOfCF4(amountOfCF4.setScale(5, RoundingMode.HALF_UP))
            .totalCF4Emissions(totalCF4Emissions.setScale(5, RoundingMode.HALF_UP))
            .amountOfC2F6(amountOfC2F6.setScale(5, RoundingMode.HALF_UP))
            .totalC2F6Emissions(totalC2F6Emissions.setScale(5, RoundingMode.HALF_UP))
            .reportableEmissions(reportableEmissions.setScale(5, RoundingMode.HALF_UP))
            .build();
    }

    private BigDecimal calculateAmountOfCF4ByOverVoltage(BigDecimal activityDataValue,
                                            OverVoltageSourceStreamEmissionCalculationMethodData calculationValues) {
        BigDecimal dividend = activityDataValue
                .multiply(calculationValues.getAnodeEffectsOverVoltagePerCell())
                .multiply(calculationValues.getOverVoltageCoefficient());
        BigDecimal divisor = calculationValues.getAluminiumAverageCurrentEfficiencyProduction().multiply(BigDecimal.TEN);
        return dividend.divide(divisor, MathContext.DECIMAL128);
    }

    private BigDecimal calculateAmountOfCF4BySlope(BigDecimal activityDataValue,
                                            SlopeSourceStreamEmissionCalculationMethodData calculationValues) {
        var intermediaryCalculation = activityDataValue
            .multiply(calculationValues.getAnodeEffectsPerCellDay())
            .multiply(calculationValues.getAverageDurationOfAnodeEffectsInMinutes())
            .multiply(calculationValues.getSlopeCF4EmissionFactor());

        return intermediaryCalculation.divide(BigDecimal.valueOf(1000));
    }
}
