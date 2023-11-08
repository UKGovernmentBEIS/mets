package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.SourceStreamCalculationParametersInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceStreamCalculationParametersInfoServiceTest {

    private SourceStreamCalculationParametersInfoService calculationParametersInfoService;

    @Mock
    private FirstCategoryEmissionsCalculationService firstCategoryEmissionsCalculationService;

    @BeforeEach
    void setUp() {
        List<SourceStreamCategoryEmissionsCalculationService> categoryEmissionsCalculationServices = List.of(firstCategoryEmissionsCalculationService);
        calculationParametersInfoService = new SourceStreamCalculationParametersInfoService(categoryEmissionsCalculationServices);
    }

    @Test
    void getCalculationParametersInfoBySourceStreamType() {
        SourceStreamType sourceStreamType = SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS;
        List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = List.of(
            CalculationParameterMeasurementUnits.builder()
                .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
                .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
                .build()
        );

        when(firstCategoryEmissionsCalculationService.getSourceStreamCategory()).thenReturn(SourceStreamCategory.CATEGORY_1);
        when(firstCategoryEmissionsCalculationService.getValidMeasurementUnitsCombinations()).thenReturn(measurementUnitsCombinations);

        SourceStreamCalculationParametersInfo calculationParametersInfo =
            calculationParametersInfoService.getCalculationParametersInfoBySourceStreamType(sourceStreamType);

        assertNotNull(calculationParametersInfo);
        assertThat(calculationParametersInfo.getApplicableTypes())
            .containsExactlyInAnyOrderElementsOf(sourceStreamType.getCategory().getApplicableCalculationParameterTypes());
        assertThat(calculationParametersInfo.getMeasurementUnitsCombinations())
            .containsExactlyInAnyOrderElementsOf(measurementUnitsCombinations);

        verify(firstCategoryEmissionsCalculationService, times(1)).getSourceStreamCategory();
        verify(firstCategoryEmissionsCalculationService, times(1)).getValidMeasurementUnitsCombinations();
    }

}