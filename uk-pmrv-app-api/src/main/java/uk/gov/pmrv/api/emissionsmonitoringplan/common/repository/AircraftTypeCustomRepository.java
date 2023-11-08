package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;

public interface AircraftTypeCustomRepository {

    @Transactional(readOnly = true)
    AircraftTypeSearchResults findBySearchCriteria(AircraftTypeSearchCriteria searchCriteria);

    @Transactional(readOnly = true)
    List<String> findInvalidDesignatorCodes(List<String> designatorCodes);
}
