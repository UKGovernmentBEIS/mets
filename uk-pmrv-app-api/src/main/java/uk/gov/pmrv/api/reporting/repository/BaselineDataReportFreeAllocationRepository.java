package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.reporting.domain.bdr.BaselineDataReportFreeAllocation;

import java.util.Optional;

@Repository
public interface BaselineDataReportFreeAllocationRepository extends JpaRepository<BaselineDataReportFreeAllocation, Long> {

    Optional<BaselineDataReportFreeAllocation> findByAccountId(Long accountId);

}
