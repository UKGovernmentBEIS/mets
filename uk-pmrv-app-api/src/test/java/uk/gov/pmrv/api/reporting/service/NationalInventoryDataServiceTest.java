package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.IpccSector;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;
import uk.gov.pmrv.api.reporting.repository.NationalInventoryDataRepository;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NationalInventoryDataServiceTest {

    @InjectMocks
    private NationalInventoryDataService nationalInventoryDataService;

    @Mock
    private NationalInventoryDataRepository nationalInventoryDataRepository;

    @Test
    void getNationalInventoryDataByReportingYear() {
        Year year = Year.of(2021);
        IpccSector sector = IpccSector.builder().name("1A1a").displayName("1A1adisplay").build();
        String fuel = "Coke";

        EmissionCalculationParams emissionCalculationParams = EmissionCalculationParams.builder()
            .emissionFactor(BigDecimal.valueOf(Math.random()))
            .netCalorificValue(BigDecimal.valueOf(Math.random()))
            .oxidationFactor(BigDecimal.valueOf(Math.random()))
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build();

        NationalInventoryData nationalInventoryData = NationalInventoryData.builder()
            .reportingYear(year)
            .sector(sector)
            .fuel(fuel)
            .emissionCalculationParams(emissionCalculationParams)
            .build();

        NationalInventoryDataDTO expected = NationalInventoryDataDTO.builder()
            .sectors(Set.of(NationalInventoryDataDTO.Sector.builder()
                    .name(sector.getName())
                            .displayName(sector.getDisplayName())
                    .fuels(Set.of(NationalInventoryDataDTO.Sector.Fuel.builder()
                            .name(fuel)
                            .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                .emissionFactor(nationalInventoryData.getEmissionCalculationParams().getEmissionFactor())
                                .netCalorificValue(nationalInventoryData.getEmissionCalculationParams().getNetCalorificValue())
                                .oxidationFactor(nationalInventoryData.getEmissionCalculationParams().getOxidationFactor())
                                .ncvMeasurementUnit(nationalInventoryData.getEmissionCalculationParams().getNcvMeasurementUnit())
                                .build())
                        .build()))
                .build()))
            .build();

        when(nationalInventoryDataRepository.findByReportingYearOrderBySectorAscFuelAsc(year))
            .thenReturn(List.of(nationalInventoryData));

        NationalInventoryDataDTO result =
            nationalInventoryDataService.getNationalInventoryDataByReportingYear(year);

        assertEquals(expected, result);

        verify(nationalInventoryDataRepository, times(1)).findByReportingYearOrderBySectorAscFuelAsc(year);
    }

    @Test
    void getEmissionCalculationParams() {
        Year year = Year.of(2022);
        IpccSector sector = IpccSector.builder().name("1A1a").displayName("1A1adisplay").build();
        String fuel = "Coke";

        EmissionCalculationParams emissionCalculationParams = EmissionCalculationParams.builder()
            .emissionFactor(BigDecimal.valueOf(Math.random()))
            .netCalorificValue(BigDecimal.valueOf(Math.random()))
            .oxidationFactor(BigDecimal.valueOf(Math.random()))
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .build();

        NationalInventoryData nationalInventoryData = NationalInventoryData.builder()
            .reportingYear(year)
            .sector(sector)
            .fuel(fuel)
            .emissionCalculationParams(emissionCalculationParams)
            .build();

        when(nationalInventoryDataRepository.findByReportingYearAndSectorAndFuel(year, sector.getName(), fuel))
            .thenReturn(Optional.of(nationalInventoryData));

        InventoryEmissionCalculationParamsDTO result =
            nationalInventoryDataService.getEmissionCalculationParams(year, sector.getName(), fuel);

        assertEquals(emissionCalculationParams.getEmissionFactor(), result.getEmissionFactor());
        assertEquals(emissionCalculationParams.getOxidationFactor(), result.getOxidationFactor());
        assertEquals(emissionCalculationParams.getNetCalorificValue(), result.getNetCalorificValue());
        assertEquals(emissionCalculationParams.getNcvMeasurementUnit(), result.getNcvMeasurementUnit());
        assertEquals(emissionCalculationParams.getEfMeasurementUnit(), result.getEfMeasurementUnit());

        verify(nationalInventoryDataRepository, times(1))
            .findByReportingYearAndSectorAndFuel(year, sector.getName(), fuel);
    }

    @Test
    void getEmissionCalculationParams_return_null_object() {
        Year year = Year.of(2022);
        String sector = "1A1a";
        String fuel = "Coke";

        when(nationalInventoryDataRepository.findByReportingYearAndSectorAndFuel(year, sector, fuel))
            .thenReturn(Optional.empty());

        assertNull(nationalInventoryDataService.getEmissionCalculationParams(year, sector, fuel));

        verify(nationalInventoryDataRepository, times(1))
            .findByReportingYearAndSectorAndFuel(year, sector, fuel);
    }

    @Test
    void getInventoryDataExistenceByYear() {
        final Year year = Year.now();
        final InventoryDataYearExistenceDTO expected = InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(true)
                .build();

        when(nationalInventoryDataRepository.existsByReportingYear(year)).thenReturn(true);

        // Invoke
        InventoryDataYearExistenceDTO actual = nationalInventoryDataService.getInventoryDataExistenceByYear(year);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(nationalInventoryDataRepository, times(1)).existsByReportingYear(year);
    }
}