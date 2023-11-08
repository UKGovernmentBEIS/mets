package uk.gov.pmrv.api.reporting.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataContinuousMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackBiomass;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmissionCalculationParamsMapperTest {

    private EmissionCalculationParamsMapper mapper = Mappers.getMapper(EmissionCalculationParamsMapper.class);

    @Test
    void toEmissionsCalculationParams_regional_data() {
        BigDecimal calculationFactor = BigDecimal.valueOf(0.989);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        BigDecimal oxidationFactor = BigDecimal.valueOf(0.999);
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);

        RegionalInventoryEmissionCalculationParamsDTO regionalInventoryEmissionCalculationParams =
                RegionalInventoryEmissionCalculationParamsDTO.builder()
                        .calculationFactor(calculationFactor)
                        .netCalorificValue(netCalorificValue)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .oxidationFactor(oxidationFactor)
                        .build();
        CalculationActivityDataContinuousMeteringCalcMethod activityDataCalcMethod =
                CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build();
        BiomassPercentages sourceStreamBiomassPercentages =
                BiomassPercentages.builder()
                        .contains(false)
                        .build();
        SourceStreamType sourceStreamType = SourceStreamType.COKE_MASS_BALANCE;

        EmissionsCalculationParamsDTO emissionsCalculationParams = mapper.toEmissionsCalculationParams(
                regionalInventoryEmissionCalculationParams,
                activityDataCalcMethod,
                sourceStreamBiomassPercentages,
                sourceStreamType);

        assertNotNull(emissionsCalculationParams);
        assertEquals(activityData, emissionsCalculationParams.getActivityData());
        assertEquals(activityDataMeasurementUnit, emissionsCalculationParams.getActivityDataMeasurementUnit());
        assertEquals(emissionFactor, emissionsCalculationParams.getEmissionFactor());
        assertEquals(efMeasurementUnit, emissionsCalculationParams.getEfMeasurementUnit());
        assertEquals(netCalorificValue, emissionsCalculationParams.getNetCalorificValue());
        assertEquals(ncvMeasurementUnit, emissionsCalculationParams.getNcvMeasurementUnit());
        assertEquals(oxidationFactor, emissionsCalculationParams.getOxidationFactor());
        assertFalse(emissionsCalculationParams.isContainsBiomass());
        assertNull(emissionsCalculationParams.getCarbonContent());
        assertNull(emissionsCalculationParams.getCarbonContentMeasurementUnit());
        assertNull(emissionsCalculationParams.getConversionFactor());
    }

    @Test
    void toMeasurementCO2EmissionsCalculationParamsDTO() {
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
                MeasurementCO2EmissionPointEmission.builder()
                        .biomassPercentages(BiomassPercentages.builder()
                                .contains(true)
                                .biomassPercentage(BigDecimal.TEN)
                                .nonSustainableBiomassPercentage(BigDecimal.ONE)
                                .build())
                        .operationalHours(BigDecimal.TEN)
                        .annualFossilAmountOfGreenhouseGas(BigDecimal.TEN)
                        .annualHourlyAverageGHGConcentration(BigDecimal.TEN)
                        .annualGasFlow(BigDecimal.ONE)
                        .globalWarmingPotential(BigDecimal.ONE)
                        .annualHourlyAverageFlueGasFlow(BigDecimal.TEN)
                        .build();

        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
                mapper.toMeasurementCO2EmissionsCalculationParamsDTO(
                        measurementEmissionPointEmission);
        assertNotNull(measurementCO2EmissionsCalculationParamsDTO);
        assertEquals(measurementCO2EmissionsCalculationParamsDTO.getBiomassPercentage(),
                measurementEmissionPointEmission.getBiomassPercentages().getBiomassPercentage());
        assertEquals(measurementCO2EmissionsCalculationParamsDTO.isContainsBiomass(),
                measurementEmissionPointEmission.getBiomassPercentages().getContains());
        assertEquals(measurementCO2EmissionsCalculationParamsDTO.getOperationalHours(),
                measurementEmissionPointEmission.getOperationalHours());
        assertEquals(measurementCO2EmissionsCalculationParamsDTO.getAnnualHourlyAverageGHGConcentration(),
                measurementEmissionPointEmission.getAnnualHourlyAverageGHGConcentration());
        assertEquals(measurementCO2EmissionsCalculationParamsDTO.getAnnualHourlyAverageFlueGasFlow(),
                measurementEmissionPointEmission.getAnnualHourlyAverageFlueGasFlow());
    }

    @Test
    void toMeasurementCO2EmissionsCalculationDTO() {
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
                MeasurementCO2EmissionPointEmission.builder()
                        .globalWarmingPotential(BigDecimal.ONE)
                        .reportableEmissions(BigDecimal.TEN)
                        .sustainableBiomassEmissions(BigDecimal.TEN)
                        .annualGasFlow(BigDecimal.ONE)
                        .annualFossilAmountOfGreenhouseGas(BigDecimal.TEN)
                        .build();

        MeasurementEmissionsCalculationDTO measurementCO2EmissionsCalculationDTO =
                mapper.toMeasurementEmissionsCalculationDTO(measurementEmissionPointEmission);
        assertNotNull(measurementCO2EmissionsCalculationDTO);
        assertEquals(measurementCO2EmissionsCalculationDTO.getReportableEmissions(),
                measurementEmissionPointEmission.getReportableEmissions());
        assertEquals(measurementCO2EmissionsCalculationDTO.getGlobalWarmingPotential(),
                measurementEmissionPointEmission.getGlobalWarmingPotential());
        assertEquals(measurementCO2EmissionsCalculationDTO.getSustainableBiomassEmissions(),
                measurementEmissionPointEmission.getSustainableBiomassEmissions());
        assertEquals(measurementCO2EmissionsCalculationDTO.getAnnualGasFlow(),
                measurementEmissionPointEmission.getAnnualGasFlow());
        assertEquals(measurementCO2EmissionsCalculationDTO.getAnnualFossilAmountOfGreenhouseGas(),
                measurementEmissionPointEmission.getAnnualFossilAmountOfGreenhouseGas());
    }

    @Test
    void toMeasurementN2OEmissionsCalculationParamsDTO() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
                MeasurementN2OEmissionPointEmission.builder()
                        .biomassPercentages(BiomassPercentages.builder()
                                .contains(true)
                                .biomassPercentage(BigDecimal.TEN)
                                .nonSustainableBiomassPercentage(BigDecimal.ONE)
                                .build())
                        .operationalHours(BigDecimal.TEN)
                        .annualFossilAmountOfGreenhouseGas(BigDecimal.TEN)
                        .annualHourlyAverageGHGConcentration(BigDecimal.TEN)
                        .annualGasFlow(BigDecimal.ONE)
                        .globalWarmingPotential(BigDecimal.ONE)
                        .annualHourlyAverageFlueGasFlow(BigDecimal.TEN)
                        .build();

        MeasurementEmissionsCalculationParamsDTO measurementN2OEmissionsCalculationParamsDTO =
                mapper.toMeasurementN2OEmissionsCalculationParamsDTO(
                        measurementEmissionPointEmission);
        assertNotNull(measurementN2OEmissionsCalculationParamsDTO);
        assertEquals(measurementN2OEmissionsCalculationParamsDTO.getBiomassPercentage(),
                measurementEmissionPointEmission.getBiomassPercentages().getBiomassPercentage());
        assertEquals(measurementN2OEmissionsCalculationParamsDTO.isContainsBiomass(),
                measurementEmissionPointEmission.getBiomassPercentages().getContains());
        assertEquals(measurementN2OEmissionsCalculationParamsDTO.getOperationalHours(),
                measurementEmissionPointEmission.getOperationalHours());
        assertEquals(measurementN2OEmissionsCalculationParamsDTO.getAnnualHourlyAverageGHGConcentration(),
                measurementEmissionPointEmission.getAnnualHourlyAverageGHGConcentration());
        assertEquals(measurementN2OEmissionsCalculationParamsDTO.getAnnualHourlyAverageFlueGasFlow(),
                measurementEmissionPointEmission.getAnnualHourlyAverageFlueGasFlow());
    }

    @Test
    void toMeasurementEmissionsCalculationDTO() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
                MeasurementN2OEmissionPointEmission.builder()
                        .globalWarmingPotential(BigDecimal.ONE)
                        .reportableEmissions(BigDecimal.TEN)
                        .sustainableBiomassEmissions(BigDecimal.TEN)
                        .annualGasFlow(BigDecimal.ONE)
                        .annualFossilAmountOfGreenhouseGas(BigDecimal.TEN)
                        .build();

        MeasurementEmissionsCalculationDTO measurementEmissionsCalculationDTO =
                mapper.toMeasurementN2OEmissionsCalculationDTO(measurementEmissionPointEmission);
        assertNotNull(measurementEmissionsCalculationDTO);
        assertEquals(measurementEmissionsCalculationDTO.getReportableEmissions(),
                measurementEmissionPointEmission.getReportableEmissions());
        assertEquals(measurementEmissionsCalculationDTO.getGlobalWarmingPotential(),
                measurementEmissionPointEmission.getGlobalWarmingPotential());
        assertEquals(measurementEmissionsCalculationDTO.getSustainableBiomassEmissions(),
                measurementEmissionPointEmission.getSustainableBiomassEmissions());
        assertEquals(measurementEmissionsCalculationDTO.getAnnualGasFlow(),
                measurementEmissionPointEmission.getAnnualGasFlow());
        assertEquals(measurementEmissionsCalculationDTO.getAnnualFossilAmountOfGreenhouseGas(),
                measurementEmissionPointEmission.getAnnualFossilAmountOfGreenhouseGas());
    }

    @Test
    void toFallbackEmissionsCalculationParamsDTO() {
        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
                .biomass(FallbackBiomass.builder()
                        .contains(true)
                        .totalNonSustainableBiomassEmissions(BigDecimal.TEN)
                        .build())
                .totalFossilEmissions(BigDecimal.TEN)
                .build();

        FallbackEmissionsCalculationParamsDTO fallbackEmissionsCalculationParamsDTO =
                mapper.toFallbackEmissionsCalculationParamsDTO(fallbackEmission);
        assertNotNull(fallbackEmissionsCalculationParamsDTO);
        assertEquals(fallbackEmissionsCalculationParamsDTO.isContainsBiomass(),
                fallbackEmission.getBiomass().getContains());
        assertEquals(fallbackEmissionsCalculationParamsDTO.getTotalNonSustainableBiomassEmissions(),
                fallbackEmission.getBiomass().getTotalNonSustainableBiomassEmissions());
        assertEquals(fallbackEmissionsCalculationParamsDTO.getTotalFossilEmissions(),
                fallbackEmission.getTotalFossilEmissions());
    }

    @Test
    void toFallbackEmissionsCalculationDTO() {
        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
                .reportableEmissions(BigDecimal.TEN)
                .build();

        FallbackEmissionsCalculationDTO fallbackEmissionsCalculationDTO =
                mapper.toFallbackEmissionsCalculationDTO(fallbackEmission);

        assertNotNull(fallbackEmissionsCalculationDTO);
        assertEquals(fallbackEmissionsCalculationDTO.getReportableEmissions(),
                fallbackEmission.getReportableEmissions());
    }

    @Test
    void toPfcEmissionsCalculationParamsDTO() {
        SlopeSourceStreamEmissionCalculationMethodData slopeSourceStreamEmissionCalculationValues = SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
                .build();
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
                .totalPrimaryAluminium(BigDecimal.ONE)
                .pfcSourceStreamEmissionCalculationMethodData(slopeSourceStreamEmissionCalculationValues)
                .build();

        PfcEmissionsCalculationParamsDTO expected = PfcEmissionsCalculationParamsDTO.builder()
                .totalPrimaryAluminium(BigDecimal.ONE)
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .pfcSourceStreamEmissionCalculationMethodData(slopeSourceStreamEmissionCalculationValues)
                .build();

        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = mapper.toPfcEmissionsCalculationParamsDTO(pfcSourceStreamEmission);
        assertEquals(pfcEmissionsCalculationParamsDTO, expected);
    }

    @Test
    void toPfcEmissionsCalculationDTO() {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
                .amountOfC2F6(BigDecimal.ONE)
                .amountOfCF4(BigDecimal.ONE)
                .totalPrimaryAluminium(BigDecimal.ONE)
                .totalCF4Emissions(BigDecimal.ONE)
                .totalC2F6Emissions(BigDecimal.ONE)
                .reportableEmissions(BigDecimal.ONE)
                .build();

        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
                .totalC2F6Emissions(BigDecimal.ONE)
                .totalCF4Emissions(BigDecimal.ONE)
                .amountOfC2F6(BigDecimal.ONE)
                .amountOfCF4(BigDecimal.ONE)
                .reportableEmissions(BigDecimal.ONE)
                .build();

        PfcEmissionsCalculationDTO pfcEmissionsCalculationDTO = mapper.toPfcEmissionsCalculationDTO(pfcSourceStreamEmission);
        assertEquals(pfcEmissionsCalculationDTO, expected);
    }
}