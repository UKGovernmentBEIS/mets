package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.RegionalInventoryData;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.repository.RegionalInventoryDataRepository;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionalInventoryDataServiceTest {

    @InjectMocks
    private RegionalInventoryDataService regionalInventoryDataService;

    @Mock
    private RegionalInventoryDataRepository regionalInventoryDataRepository;

    @Test
    void getRegionalEmissionCalculationParameters() {
        Year year = Year.now();
        String chargingZoneCode= "EA";

        EmissionCalculationParams emissionCalculationParams = EmissionCalculationParams.builder()
            .emissionFactor(BigDecimal.valueOf(Math.random()))
            .netCalorificValue(BigDecimal.valueOf(Math.random()))
            .oxidationFactor(BigDecimal.valueOf(Math.random()))
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
            .build();

        RegionalInventoryData regionalInventoryData = RegionalInventoryData.builder()
            .reportingYear(year)
            .chargingZone(ChargingZone.builder().id(1L).code(chargingZoneCode).build())
            .emissionCalculationParams(emissionCalculationParams)
            .calculationFactor(BigDecimal.valueOf(Math.random()))
            .build();

        when(regionalInventoryDataRepository.findByReportingYearAndChargingZoneCode(year, chargingZoneCode))
            .thenReturn(Optional.of(regionalInventoryData));

        Optional<RegionalInventoryEmissionCalculationParamsDTO> regionalEmissionCalculationParametersOpt =
            regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode);

        assertThat(regionalEmissionCalculationParametersOpt).isNotEmpty();
        RegionalInventoryEmissionCalculationParamsDTO regionalEmissionCalculationParameters = regionalEmissionCalculationParametersOpt.get();
        assertEquals(emissionCalculationParams.getEmissionFactor(), regionalEmissionCalculationParameters.getEmissionFactor());
        assertEquals(emissionCalculationParams.getNetCalorificValue(), regionalEmissionCalculationParameters.getNetCalorificValue());
        assertEquals(emissionCalculationParams.getOxidationFactor(), regionalEmissionCalculationParameters.getOxidationFactor());
        assertEquals(emissionCalculationParams.getNcvMeasurementUnit(), regionalEmissionCalculationParameters.getNcvMeasurementUnit());
        assertEquals(emissionCalculationParams.getEfMeasurementUnit(), regionalEmissionCalculationParameters.getEfMeasurementUnit());
        assertEquals(regionalInventoryData.getCalculationFactor(), regionalEmissionCalculationParameters.getCalculationFactor());

        verify(regionalInventoryDataRepository, times(1)).findByReportingYearAndChargingZoneCode(year, chargingZoneCode);
    }

    @Test
    void getRegionalEmissionCalculationParameters_not_found() {
        Year year = Year.now();
        String chargingZoneCode= "EA";

        when(regionalInventoryDataRepository.findByReportingYearAndChargingZoneCode(year, chargingZoneCode))
            .thenReturn(Optional.empty());

        assertThat(regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode)).isEmpty();

        verify(regionalInventoryDataRepository, times(1))
            .findByReportingYearAndChargingZoneCode(year, chargingZoneCode);
    }

    @Test
    void getInventoryDataExistenceByYear() {
        final Year year = Year.now();
        final InventoryDataYearExistenceDTO expected = InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(true)
                .build();

        when(regionalInventoryDataRepository.existsByReportingYear(year)).thenReturn(true);

        // Invoke
        InventoryDataYearExistenceDTO actual = regionalInventoryDataService.getInventoryDataExistenceByYear(year);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(regionalInventoryDataRepository, times(1)).existsByReportingYear(year);
    }
}