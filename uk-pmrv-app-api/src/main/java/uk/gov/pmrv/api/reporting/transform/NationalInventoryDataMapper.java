package uk.gov.pmrv.api.reporting.transform;

import lombok.experimental.UtilityClass;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.reporting.domain.IpccSector;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class NationalInventoryDataMapper {

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMETERS_MAPPER = Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public NationalInventoryDataDTO toNationalInventoryDataDTO(List<NationalInventoryData> nationalInventoryData) {
        Map<IpccSector, Map<String, InventoryEmissionCalculationParamsDTO>> emissionCalcParamsBySectorAndFuel =
            nationalInventoryData.stream().collect(
                Collectors.groupingBy(NationalInventoryData::getSector, LinkedHashMap::new,
                    Collectors.toMap(
                        NationalInventoryData::getFuel,
                        data -> EMISSION_CALCULATION_PARAMETERS_MAPPER.toInventoryEmissionCalculationParamsDTO(data.getEmissionCalculationParams()),
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new)
                )
            );

        Set<NationalInventoryDataDTO.Sector> sectors = new LinkedHashSet<>();

        emissionCalcParamsBySectorAndFuel.forEach((sector, sectorFuels) -> {
            Set<NationalInventoryDataDTO.Sector.Fuel> fuels = new LinkedHashSet<>();

            sectorFuels.forEach((fuel, emissionCalcParams) ->
                fuels.add(NationalInventoryDataDTO.Sector.Fuel.builder().name(fuel).emissionCalculationParameters(emissionCalcParams).build())
            );

            sectors.add(NationalInventoryDataDTO.Sector.builder().name(sector.getName()).displayName(sector.getDisplayName()).fuels(fuels).build());
        });

        return NationalInventoryDataDTO.builder().sectors(sectors).build();
    }
}
