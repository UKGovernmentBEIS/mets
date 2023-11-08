package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.AircraftTypeRepository;

@Service
@RequiredArgsConstructor
public class AircraftTypeQueryService {

    private final AircraftTypeRepository aircraftTypeRepository;

    public AircraftTypeSearchResults getAircraftTypesBySearchCriteria(AircraftTypeSearchCriteria searchCriteria) {
        return aircraftTypeRepository.findBySearchCriteria(searchCriteria);
    }

    public List<String> findInvalidDesignatorCodes(List<String> designatorCodes) {
        return aircraftTypeRepository.findInvalidDesignatorCodes(designatorCodes);
    }
}
