package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftTypeId;

@Repository
public interface AircraftTypeRepository extends JpaRepository<AircraftType, AircraftTypeId>, AircraftTypeCustomRepository {
}
