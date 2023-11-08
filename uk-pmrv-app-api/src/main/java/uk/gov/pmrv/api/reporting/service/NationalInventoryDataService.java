package uk.gov.pmrv.api.reporting.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;
import uk.gov.pmrv.api.reporting.repository.NationalInventoryDataRepository;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;
import uk.gov.pmrv.api.reporting.transform.NationalInventoryDataMapper;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NationalInventoryDataService {

    private final NationalInventoryDataRepository nationalInventoryDataRepository;
    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public NationalInventoryDataDTO getNationalInventoryDataByReportingYear(Year reportingYear) {
        List<NationalInventoryData> nationalInventoryData =
            nationalInventoryDataRepository.findByReportingYearOrderBySectorAscFuelAsc(reportingYear);
        return NationalInventoryDataMapper.toNationalInventoryDataDTO(nationalInventoryData);
    }

    public InventoryEmissionCalculationParamsDTO getEmissionCalculationParams(Year reportingYear, String sector, String fuel) {
        return nationalInventoryDataRepository
            .findByReportingYearAndSectorAndFuel(reportingYear, sector, fuel)
            .map(data -> EMISSION_CALCULATION_PARAMS_MAPPER.toInventoryEmissionCalculationParamsDTO(data.getEmissionCalculationParams()))
            .orElse(null);
    }

    public InventoryDataYearExistenceDTO getInventoryDataExistenceByYear(Year year) {
        return InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(nationalInventoryDataRepository.existsByReportingYear(year))
                .build();
    }
}
