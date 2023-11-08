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
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataContinuousMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationSourceStreamManualEmissionsValidatorTest {

    @InjectMocks
    private AerCalculationSourceStreamManualEmissionsValidator validator;

    @Mock
    private EmissionsCalculationService emissionsCalculationService;

    @Test
    void validate_valid() {
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);
        BigDecimal totalFossilEmissions = BigDecimal.valueOf(145098.08765);
        BigDecimal conversionFactor = BigDecimal.valueOf(0.0911);

        CalculationManualEmissionCalculationParamValues calcManualEmissionCalculationParamValues =
                CalculationManualEmissionCalculationParamValues.builder()
                        .netCalorificValue(netCalorificValue)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .conversionFactor(conversionFactor)
                        .totalReportableEmissions(totalFossilEmissions)
                        .build();
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .emissionCalculationParamValues(calcManualEmissionCalculationParamValues)
                .calculationActivityDataCalculationMethod(CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .biomassPercentages(BiomassPercentages.builder().build())
                .build();

        SourceStreamType sourceStreamType = SourceStreamType.CERAMICS_ALKALI_OXIDE_METHOD_B;
        Year reportingYear = Year.of(2022);

        EmissionsCalculationParamsDTO emissionsCalculationParams = EmissionsCalculationParamsDTO.builder()
                .activityData(activityData)
                .activityDataMeasurementUnit(activityDataMeasurementUnit)
                .containsBiomass(false)
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .conversionFactor(conversionFactor)
                .sourceStreamType(sourceStreamType)
                .build();
        EmissionsCalculationDTO emissions = EmissionsCalculationDTO.builder()
                .reportableEmissions(totalFossilEmissions)
                .build();

        when(emissionsCalculationService.calculateEmissions(emissionsCalculationParams)).thenReturn(emissions);

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, sourceStreamType, reportingYear);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_invalid() {
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03875);
        NCVMeasurementUnit ncvMeasurementUnit = NCVMeasurementUnit.GJ_PER_TONNE;
        BigDecimal emissionFactor = BigDecimal.valueOf(347.89);
        EmissionFactorMeasurementUnit efMeasurementUnit = EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ;
        ActivityDataMeasurementUnit activityDataMeasurementUnit = ActivityDataMeasurementUnit.TONNES;
        BigDecimal activityData = BigDecimal.valueOf(2908.07);
        BigDecimal totalFossilEmissions = BigDecimal.valueOf(145098.08765);
        BigDecimal totalBiomassEmissions = BigDecimal.valueOf(2500.12);
        BigDecimal conversionFactor = BigDecimal.valueOf(0.0911);

        CalculationManualEmissionCalculationParamValues calcManualEmissionCalculationParamValues =
                CalculationManualEmissionCalculationParamValues.builder()
                        .netCalorificValue(netCalorificValue)
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .emissionFactor(emissionFactor)
                        .efMeasurementUnit(efMeasurementUnit)
                        .conversionFactor(conversionFactor)
                        .totalReportableEmissions(totalFossilEmissions)
                        .totalSustainableBiomassEmissions(totalBiomassEmissions)
                        .calculationCorrect(false)
                        .providedEmissions(ManuallyProvidedEmissions.builder().build())
                        .build();
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .emissionCalculationParamValues(calcManualEmissionCalculationParamValues)
                .calculationActivityDataCalculationMethod(CalculationActivityDataContinuousMeteringCalcMethod.builder()
                        .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                        .measurementUnit(activityDataMeasurementUnit)
                        .totalMaterial(activityData)
                        .activityData(activityData)
                        .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .biomassPercentages(BiomassPercentages.builder().contains(true).build())
                .build();

        SourceStreamType sourceStreamType = SourceStreamType.CERAMICS_ALKALI_OXIDE_METHOD_B;
        Year reportingYear = Year.of(2022);

        EmissionsCalculationParamsDTO emissionsCalculationParams = EmissionsCalculationParamsDTO.builder()
                .activityData(activityData)
                .activityDataMeasurementUnit(activityDataMeasurementUnit)
                .containsBiomass(true)
                .netCalorificValue(netCalorificValue)
                .ncvMeasurementUnit(ncvMeasurementUnit)
                .emissionFactor(emissionFactor)
                .efMeasurementUnit(efMeasurementUnit)
                .conversionFactor(conversionFactor)
                .sourceStreamType(sourceStreamType)
                .build();
        EmissionsCalculationDTO emissions = EmissionsCalculationDTO.builder()
                .reportableEmissions(BigDecimal.valueOf(145098.087))
                .build();

        when(emissionsCalculationService.calculateEmissions(emissionsCalculationParams)).thenReturn(emissions);

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, sourceStreamType, reportingYear);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(2, aerViolations.size());

        assertThat(aerViolations).extracting(AerViolation::getSectionName).containsOnly("CalculationOfCO2Emissions");
        assertThat(aerViolations).extracting(AerViolation::getMessage).containsExactlyInAnyOrder(
                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_EMISSIONS.getMessage(),
                AerViolation.AerViolationMessage.TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING.getMessage()
        );
    }

    @Test
    void getParameterCalculationMethodType() {
        assertEquals(CalculationParameterCalculationMethodType.MANUAL, validator.getParameterCalculationMethodType());
    }
}