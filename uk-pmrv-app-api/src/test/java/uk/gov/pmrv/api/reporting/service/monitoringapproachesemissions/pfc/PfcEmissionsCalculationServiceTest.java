package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Year;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.OverVoltageSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

class PfcEmissionsCalculationServiceTest {

    private final PfcEmissionsCalculationService pfcEmissionsCalculationService = new PfcEmissionsCalculationService();

    @Test
    void calculateEmissionsForSlopeMethod_legacyYear() {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .reportingYear(Year.of(2025))
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.TEN)
                .anodeEffectsPerCellDay(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.TEN)
                .c2F6WeightFraction(BigDecimal.TEN)
                .build())
            .build();

        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .globalWarmingPotentialCF4(new BigDecimal("7390"))
            .globalWarmingPotentialC2F6(new BigDecimal("12200"))
            .amountOfCF4(new BigDecimal("0.10000"))
            .totalCF4Emissions(new BigDecimal("739.00000"))
            .amountOfC2F6(new BigDecimal("1.00000"))
            .totalC2F6Emissions(new BigDecimal("12200.00000"))
            .reportableEmissions(new BigDecimal("129390.00000"))
            .build();

        PfcEmissionsCalculationDTO actual = pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO);

        assertEquals(expected, actual);
    }

    @Test
    void calculateEmissionsForSlopeMethod_updatedGwpYear() {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .reportingYear(Year.of(2026))
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.TEN)
                .anodeEffectsPerCellDay(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.TEN)
                .c2F6WeightFraction(BigDecimal.TEN)
                .build())
            .build();

        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .globalWarmingPotentialCF4(new BigDecimal("6630"))
            .globalWarmingPotentialC2F6(new BigDecimal("11100"))
            .amountOfCF4(new BigDecimal("0.10000"))
            .totalCF4Emissions(new BigDecimal("663.00000"))
            .amountOfC2F6(new BigDecimal("1.00000"))
            .totalC2F6Emissions(new BigDecimal("11100.00000"))
            .reportableEmissions(new BigDecimal("117630.00000"))
            .build();

        PfcEmissionsCalculationDTO actual = pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO);

        assertEquals(expected, actual);
    }

    @Test
    void calculateEmissionsForOverVoltageMethod_legacyYear() {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .reportingYear(Year.of(2025))
            .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .overVoltageCoefficient(BigDecimal.ONE)
                .anodeEffectsOverVoltagePerCell(BigDecimal.TEN)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .build())
            .build();

        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .globalWarmingPotentialCF4(new BigDecimal("7390"))
            .globalWarmingPotentialC2F6(new BigDecimal("12200"))
            .amountOfCF4(new BigDecimal("0.10000"))
            .totalCF4Emissions(new BigDecimal("739.00000"))
            .amountOfC2F6(new BigDecimal("0.10000"))
            .totalC2F6Emissions(new BigDecimal("1220.00000"))
            .reportableEmissions(new BigDecimal("195900.00000"))
            .build();

        PfcEmissionsCalculationDTO actual = pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO);

        assertEquals(expected, actual);
    }

    @Test
    void calculateEmissionsForOverVoltageMethod_updatedGwpYear() {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .reportingYear(Year.of(2026))
            .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .overVoltageCoefficient(BigDecimal.ONE)
                .anodeEffectsOverVoltagePerCell(BigDecimal.TEN)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .build())
            .build();

        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .globalWarmingPotentialCF4(new BigDecimal("6630"))
            .globalWarmingPotentialC2F6(new BigDecimal("11100"))
            .amountOfCF4(new BigDecimal("0.10000"))
            .totalCF4Emissions(new BigDecimal("663.00000"))
            .amountOfC2F6(new BigDecimal("0.10000"))
            .totalC2F6Emissions(new BigDecimal("1110.00000"))
            .reportableEmissions(new BigDecimal("177300.00000"))
            .build();

        PfcEmissionsCalculationDTO actual = pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO);

        assertEquals(expected, actual);
    }

}
