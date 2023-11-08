package uk.gov.pmrv.api.allowance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.allowance.domain.AllowanceAllocationEntity;

import java.util.Set;

@Repository
public interface AllowanceAllocationRepository extends JpaRepository<AllowanceAllocationEntity, Long> {

    @Transactional(readOnly = true)
    Set<AllowanceAllocationEntity> findAllByAccountId(Long accountId);
}
