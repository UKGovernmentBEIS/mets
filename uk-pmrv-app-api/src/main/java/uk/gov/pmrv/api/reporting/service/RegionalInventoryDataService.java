package uk.gov.pmrv.api.reporting.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.repository.RegionalInventoryDataRepository;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionalInventoryDataService {

    private final RegionalInventoryDataRepository regionalInventoryDataRepository;
    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER = Mappers.getMapper(EmissionCalculationParamsMapper.class);

    public Optional<RegionalInventoryEmissionCalculationParamsDTO> getRegionalInventoryEmissionCalculationParams(Year reportingYear,
                                                                                                                 String chargingZoneCode) {
        return regionalInventoryDataRepository
            .findByReportingYearAndChargingZoneCode(reportingYear, chargingZoneCode)
            .map(regionalInventoryData ->
                EMISSION_CALCULATION_PARAMS_MAPPER.toRegionalInventoryEmissionCalculationParamsDTO(
                    regionalInventoryData.getEmissionCalculationParams(),
                    regionalInventoryData.getCalculationFactor()
                )
            );

    }

    public InventoryDataYearExistenceDTO getInventoryDataExistenceByYear(Year year) {
        return InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(regionalInventoryDataRepository.existsByReportingYear(year))
                .build();
    }
}
