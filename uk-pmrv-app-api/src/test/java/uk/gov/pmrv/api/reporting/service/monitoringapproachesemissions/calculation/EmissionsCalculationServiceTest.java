package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsCalculationServiceTest {

    private EmissionsCalculationService emissionsCalculationService;

    @Mock
    private FirstCategoryEmissionsCalculationService firstCategoryEmissionsCalculationService;

    @BeforeEach
    void setUp() {
        List<SourceStreamCategoryEmissionsCalculationService> categoryEmissionsCalculationServices = List.of(firstCategoryEmissionsCalculationService);
        emissionsCalculationService = new EmissionsCalculationService(categoryEmissionsCalculationServices);
    }

    @Test
    void calculateTotalEmissions() {
        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_FLARES;
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(BigDecimal.ONE)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(true)
            .biomassPercentage(BigDecimal.TEN)
            .emissionFactor(BigDecimal.TEN)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .netCalorificValue(BigDecimal.ONE)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .sourceStreamType(sourceStreamType)
            .build();
        CalculationParameterMeasurementUnits calculationParameterMeasurementUnits = CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build();
        EmissionsCalculationDTO expectedEmissionsCalculation = EmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.valueOf(500.12))
            .reportableEmissions(BigDecimal.valueOf(200.32))
            .build();

        when(firstCategoryEmissionsCalculationService.getSourceStreamCategory()).thenReturn(SourceStreamCategory.CATEGORY_1);
        when(firstCategoryEmissionsCalculationService.calculateEmissions(calculationParams)).thenReturn(expectedEmissionsCalculation);

        EmissionsCalculationDTO result = emissionsCalculationService.calculateEmissions(calculationParams);

        assertEquals(expectedEmissionsCalculation, result);

        verify(firstCategoryEmissionsCalculationService, times(1)).getSourceStreamCategory();
        verify(firstCategoryEmissionsCalculationService, times(1)).calculateEmissions(calculationParams);
    }

    @Test
    void calculateTotalEmissions_exception_when_no_calculation_service_found_for_input_source_stream() {
        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_GAS_PROCESSING_TERMINALS;
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(BigDecimal.ONE)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(true)
            .biomassPercentage(BigDecimal.TEN)
            .emissionFactor(BigDecimal.TEN)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .netCalorificValue(BigDecimal.ONE)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .sourceStreamType(sourceStreamType)
            .build();

        when(firstCategoryEmissionsCalculationService.getSourceStreamCategory()).thenReturn(SourceStreamCategory.CATEGORY_1);

        BusinessException be = assertThrows(BusinessException.class,
            () -> emissionsCalculationService.calculateEmissions(calculationParams));

        assertEquals(ErrorCode.AER_EMISSIONS_CALCULATION_FAILED, be.getErrorCode());

        verify(firstCategoryEmissionsCalculationService, times(1)).getSourceStreamCategory();
        verifyNoMoreInteractions(firstCategoryEmissionsCalculationService);
    }
}