package uk.gov.pmrv.api.reporting.transform;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.EmissionCalculationParams;
import uk.gov.pmrv.api.reporting.domain.IpccSector;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NationalInventoryDataMapperTest {

    @Test
    void toNationalInventoryDataDTO() {
        Year year2021 = Year.of(2021);
        IpccSector sector1A1a = IpccSector.builder().id(1L).name("1A1a").displayName("1A1adisplay").build();
        IpccSector sector1A1v = IpccSector.builder().id(2L).name("1A1v").displayName("1A1vdisplay").build();
        IpccSector sector1A2a = IpccSector.builder().id(3L).name("1A2a").displayName("1A2adisplay").build();
        String fuelCoke = "Coke";
        String fuelGas = "Gas";
        String fuelPetroleum = "Petroleum";
        NationalInventoryData data2021_1A1a_Gas = buildNationalInventoryData(year2021, sector1A1a, fuelGas, NCVMeasurementUnit.GJ_PER_NM3);
        NationalInventoryData data2021_1A1a_Petroleum = buildNationalInventoryData(year2021, sector1A1a, fuelPetroleum, NCVMeasurementUnit.GJ_PER_TONNE);
        NationalInventoryData data2021_1A1v_Gas = buildNationalInventoryData(year2021, sector1A1v, fuelGas, NCVMeasurementUnit.GJ_PER_NM3);
        NationalInventoryData data2021_1A2a_Coke = buildNationalInventoryData(year2021, sector1A2a, fuelCoke, NCVMeasurementUnit.GJ_PER_NM3);
        NationalInventoryData data2021_1A2a_Petroleum = buildNationalInventoryData(year2021, sector1A2a, fuelPetroleum, NCVMeasurementUnit.GJ_PER_TONNE);

        NationalInventoryDataDTO nationalInventoryDataDTO = NationalInventoryDataMapper.toNationalInventoryDataDTO(
                List.of(data2021_1A1a_Gas, data2021_1A1a_Petroleum, data2021_1A2a_Petroleum, data2021_1A2a_Coke, data2021_1A1v_Gas)
        );

        assertNotNull(nationalInventoryDataDTO);
        Set<NationalInventoryDataDTO.Sector> sectors = nationalInventoryDataDTO.getSectors();
        assertThat(sectors).containsExactly(
                NationalInventoryDataDTO.Sector.builder()
                        .name(sector1A1a.getName())
                        .displayName(sector1A1a.getDisplayName())
                        .fuels(Set.of(
                                        NationalInventoryDataDTO.Sector.Fuel.builder()
                                                .name(fuelGas)
                                                .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                                        .emissionFactor(data2021_1A1a_Gas.getEmissionCalculationParams().getEmissionFactor())
                                                        .netCalorificValue(data2021_1A1a_Gas.getEmissionCalculationParams().getNetCalorificValue())
                                                        .oxidationFactor(data2021_1A1a_Gas.getEmissionCalculationParams().getOxidationFactor())
                                                        .ncvMeasurementUnit(data2021_1A1a_Gas.getEmissionCalculationParams().getNcvMeasurementUnit())
                                                        .build())
                                                .build(),
                                        NationalInventoryDataDTO.Sector.Fuel.builder()
                                                .name(fuelPetroleum)
                                                .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                                        .emissionFactor(data2021_1A1a_Petroleum.getEmissionCalculationParams().getEmissionFactor())
                                                        .netCalorificValue(data2021_1A1a_Petroleum.getEmissionCalculationParams().getNetCalorificValue())
                                                        .oxidationFactor(data2021_1A1a_Petroleum.getEmissionCalculationParams().getOxidationFactor())
                                                        .ncvMeasurementUnit(data2021_1A1a_Petroleum.getEmissionCalculationParams().getNcvMeasurementUnit())
                                                        .build())
                                                .build()
                                )
                        )
                        .build(),
                NationalInventoryDataDTO.Sector.builder()
                        .name(sector1A2a.getName())
                        .displayName(sector1A2a.getDisplayName())
                        .fuels(Set.of(
                                        NationalInventoryDataDTO.Sector.Fuel.builder()
                                                .name(fuelPetroleum)
                                                .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                                        .emissionFactor(data2021_1A2a_Petroleum.getEmissionCalculationParams().getEmissionFactor())
                                                        .netCalorificValue(data2021_1A2a_Petroleum.getEmissionCalculationParams().getNetCalorificValue())
                                                        .oxidationFactor(data2021_1A2a_Petroleum.getEmissionCalculationParams().getOxidationFactor())
                                                        .ncvMeasurementUnit(data2021_1A2a_Petroleum.getEmissionCalculationParams().getNcvMeasurementUnit())
                                                        .build())
                                                .build(),
                                        NationalInventoryDataDTO.Sector.Fuel.builder()
                                                .name(fuelCoke)
                                                .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                                        .emissionFactor(data2021_1A2a_Coke.getEmissionCalculationParams().getEmissionFactor())
                                                        .netCalorificValue(data2021_1A2a_Coke.getEmissionCalculationParams().getNetCalorificValue())
                                                        .oxidationFactor(data2021_1A2a_Coke.getEmissionCalculationParams().getOxidationFactor())
                                                        .ncvMeasurementUnit(data2021_1A2a_Coke.getEmissionCalculationParams().getNcvMeasurementUnit())
                                                        .build())
                                                .build()
                                )
                        )
                        .build(),
                NationalInventoryDataDTO.Sector.builder()
                        .name(sector1A1v.getName())
                        .displayName(sector1A1v.getDisplayName())
                        .fuels(Set.of(
                                        NationalInventoryDataDTO.Sector.Fuel.builder()
                                                .name(fuelGas)
                                                .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                                                        .emissionFactor(data2021_1A1v_Gas.getEmissionCalculationParams().getEmissionFactor())
                                                        .netCalorificValue(data2021_1A1v_Gas.getEmissionCalculationParams().getNetCalorificValue())
                                                        .oxidationFactor(data2021_1A1v_Gas.getEmissionCalculationParams().getOxidationFactor())
                                                        .ncvMeasurementUnit(data2021_1A1v_Gas.getEmissionCalculationParams().getNcvMeasurementUnit())
                                                        .build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    private NationalInventoryData buildNationalInventoryData(Year reportingYear, IpccSector sector, String fuel, NCVMeasurementUnit ncvMeasurementUnit) {
        return NationalInventoryData.builder()
                .reportingYear(reportingYear)
                .sector(sector)
                .fuel(fuel)
                .emissionCalculationParams(EmissionCalculationParams.builder()
                        .emissionFactor(BigDecimal.valueOf(Math.random()))
                        .netCalorificValue(BigDecimal.valueOf(Math.random()))
                        .oxidationFactor(BigDecimal.valueOf(Math.random()))
                        .ncvMeasurementUnit(ncvMeasurementUnit)
                        .build())
                .build();
    }
}