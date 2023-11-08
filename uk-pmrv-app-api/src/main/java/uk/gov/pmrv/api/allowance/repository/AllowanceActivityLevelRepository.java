package uk.gov.pmrv.api.allowance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;

import java.util.List;

@Repository
public interface AllowanceActivityLevelRepository extends JpaRepository<AllowanceActivityLevelEntity, Long> {

    @Transactional(readOnly = true)
    List<AllowanceActivityLevelEntity> findAllByAccountId(Long accountId);
}
