package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataContinuousMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNationalInventoryDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.service.NationalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationSourceStreamNationalInventoryDataEmissionsValidatorTest {

    @InjectMocks
    private AerCalculationSourceStreamNationalInventoryDataEmissionsValidator validator;

    @Mock
    private NationalInventoryDataService nationalInventoryDataService;

    @Mock
    private EmissionsCalculationService emissionsCalculationService;

    @Test
    void validate_valid() {
        String sector = "1A1a";
        String fuel = "Gas";
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        BigDecimal oxidationFactor = BigDecimal.valueOf(0.999);
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);
        BigDecimal biomassPercentage = BigDecimal.TEN;
        BigDecimal totalFossilEmissions = BigDecimal.valueOf(145098.08765);
        BigDecimal totalBiomassEmissions = BigDecimal.valueOf(1908.11);

        CalculationInventoryEmissionCalculationParamValues calcInventoryEmissionCalculationParamValues =
                CalculationInventoryEmissionCalculationParamValues.builder()
                        .oxidationFactor(oxidationFactor)
                        .netCalorificValue(netCalorificValue)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .totalReportableEmissions(totalFossilEmissions)
                        .totalSustainableBiomassEmissions(totalBiomassEmissions)
                        .calculationCorrect(false)
                        .providedEmissions(ManuallyProvidedEmissions.builder()
                                .totalProvidedReportableEmissions(totalFossilEmissions.add(BigDecimal.TEN))
                                .totalProvidedSustainableBiomassEmissions(totalBiomassEmissions.add(BigDecimal.TEN))
                                .build())
                        .build();
        CalculationNationalInventoryDataCalculationMethod nationalInventoryDataCalculationMethod = CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .mainActivitySector(sector)
                .fuel(fuel)
                .emissionCalculationParamValues(calcInventoryEmissionCalculationParamValues)
                .calculationActivityDataCalculationMethod(CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(nationalInventoryDataCalculationMethod)
                .biomassPercentages(BiomassPercentages.builder().contains(true).biomassPercentage(biomassPercentage).build())
                .build();

        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_FLARES;
        Year reportingYear = Year.of(2022);

        InventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams = InventoryEmissionCalculationParamsDTO.builder()
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .oxidationFactor(oxidationFactor)
                .build();

        EmissionsCalculationParamsDTO emissionsCalculationParams = EmissionsCalculationParamsDTO.builder()
                .activityData(activityData)
                .activityDataMeasurementUnit(activityDataMeasurementUnit)
                .containsBiomass(true)
                .biomassPercentage(biomassPercentage)
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .oxidationFactor(oxidationFactor)
                .sourceStreamType(sourceStreamType)
                .build();
        EmissionsCalculationDTO emissions = EmissionsCalculationDTO.builder()
                .reportableEmissions(totalFossilEmissions)
                .sustainableBiomassEmissions(totalBiomassEmissions)
                .build();

        when(nationalInventoryDataService.getEmissionCalculationParams(reportingYear, sector, fuel))
                .thenReturn(inventoryEmissionCalculationParams);
        when(emissionsCalculationService.calculateEmissions(emissionsCalculationParams)).thenReturn(emissions);

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, sourceStreamType, reportingYear);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_invalid_when_inventory_data_do_not_exist() {
        String sector = "1A1a";
        String fuel = "Gas";
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        BigDecimal oxidationFactor = BigDecimal.valueOf(0.999);
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);
        BigDecimal biomassPercentage = BigDecimal.TEN;
        BigDecimal totalFossilEmissions = BigDecimal.valueOf(145098.08765);

        CalculationInventoryEmissionCalculationParamValues calcInventoryEmissionCalculationParamValues =
                CalculationInventoryEmissionCalculationParamValues.builder()
                        .oxidationFactor(oxidationFactor)
                        .netCalorificValue(netCalorificValue)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .totalReportableEmissions(totalFossilEmissions)
                        .totalSustainableBiomassEmissions(BigDecimal.valueOf(1908.12))
                        .build();
        CalculationNationalInventoryDataCalculationMethod nationalInventoryDataCalculationMethod = CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .mainActivitySector(sector)
                .fuel(fuel)
                .emissionCalculationParamValues(calcInventoryEmissionCalculationParamValues)
                .calculationActivityDataCalculationMethod(CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(nationalInventoryDataCalculationMethod)
                .biomassPercentages(BiomassPercentages.builder().contains(true).biomassPercentage(biomassPercentage).build())
                .build();

        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_FLARES;
        Year reportingYear = Year.of(2022);

        when(nationalInventoryDataService.getEmissionCalculationParams(reportingYear, sector, fuel)).thenReturn(null);

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, sourceStreamType, reportingYear);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INVALID_CALCULATION_PARAMETERS.getMessage(), violation.getMessage());

        verifyNoInteractions(emissionsCalculationService);
    }

    @Test
    void validate_invalid() {
        String sector = "1A1a";
        String fuel = "Gas";
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        BigDecimal oxidationFactor = BigDecimal.valueOf(0.999);
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);
        BigDecimal biomassPercentage = BigDecimal.TEN;
        BigDecimal totalFossilEmissions = BigDecimal.valueOf(145098.08765);
        BigDecimal totalBiomassEmissions = BigDecimal.valueOf(1908.11);

        CalculationInventoryEmissionCalculationParamValues calcInventoryEmissionCalculationParamValues =
                CalculationInventoryEmissionCalculationParamValues.builder()
                        .oxidationFactor(oxidationFactor)
                        .netCalorificValue(BigDecimal.ONE)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .totalReportableEmissions(totalFossilEmissions)
                        .totalSustainableBiomassEmissions(BigDecimal.valueOf(1908.12))
                        .calculationCorrect(false)
                        .providedEmissions(ManuallyProvidedEmissions.builder().build())
                        .build();
        CalculationNationalInventoryDataCalculationMethod nationalInventoryDataCalculationMethod = CalculationNationalInventoryDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                .mainActivitySector(sector)
                .fuel(fuel)
                .emissionCalculationParamValues(calcInventoryEmissionCalculationParamValues)
                .calculationActivityDataCalculationMethod(CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(nationalInventoryDataCalculationMethod)
                .biomassPercentages(BiomassPercentages.builder().contains(true).biomassPercentage(biomassPercentage).build())
                .build();

        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_FLARES;
        Year reportingYear = Year.of(2022);

        InventoryEmissionCalculationParamsDTO inventoryEmissionCalculationParams = InventoryEmissionCalculationParamsDTO.builder()
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .oxidationFactor(oxidationFactor)
                .build();

        EmissionsCalculationParamsDTO emissionsCalculationParams = EmissionsCalculationParamsDTO.builder()
                .activityData(activityData)
                .activityDataMeasurementUnit(activityDataMeasurementUnit)
                .containsBiomass(true)
                .biomassPercentage(biomassPercentage)
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .oxidationFactor(oxidationFactor)
                .sourceStreamType(sourceStreamType)
                .build();
        EmissionsCalculationDTO emissions = EmissionsCalculationDTO.builder()
                .reportableEmissions(totalFossilEmissions)
                .sustainableBiomassEmissions(totalBiomassEmissions)
                .build();

        when(nationalInventoryDataService.getEmissionCalculationParams(reportingYear, sector, fuel))
                .thenReturn(inventoryEmissionCalculationParams);
        when(emissionsCalculationService.calculateEmissions(emissionsCalculationParams)).thenReturn(emissions);

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, sourceStreamType, reportingYear);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(3, aerViolations.size());

        assertThat(aerViolations).extracting(AerViolation::getSectionName).containsOnly("CalculationOfCO2Emissions");
        assertThat(aerViolations).extracting(AerViolation::getMessage).containsExactlyInAnyOrder(
                AerViolation.AerViolationMessage.CALCULATION_INVALID_CALCULATION_PARAMETERS.getMessage(),
                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_EMISSIONS.getMessage(),
                AerViolation.AerViolationMessage.TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING.getMessage()
        );
    }

    @Test
    void getParameterCalculationMethodType() {
        assertEquals(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA, validator.getParameterCalculationMethodType());
    }
}