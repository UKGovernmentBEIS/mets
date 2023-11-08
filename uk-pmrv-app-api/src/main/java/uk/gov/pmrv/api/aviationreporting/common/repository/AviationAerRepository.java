package uk.gov.pmrv.api.aviationreporting.common.repository;

import java.time.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerEntity;

@Repository
public interface AviationAerRepository extends JpaRepository<AviationAerEntity, String> {

    @Transactional(readOnly = true)
    boolean existsByAccountIdAndYear(Long accountId, Year year);
}
