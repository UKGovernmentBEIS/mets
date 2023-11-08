package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;

import java.util.List;

@Repository
public interface ChargingZoneRepository extends JpaRepository<ChargingZone, Long> {

    @Transactional(readOnly = true)
    List<ChargingZone> findByPostCodesCodeIgnoreCase(String code);
}
