package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.AerEntity;

import java.time.Year;

@Repository
public interface AerRepository extends JpaRepository<AerEntity, String> {

    @Transactional(readOnly = true)
    boolean existsByAccountIdAndYear(Long accountId, Year year);
}
