package uk.gov.pmrv.api.settings.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.settings.domain.FeeHistory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Repository
public interface FeeHistoryRepository extends JpaRepository<FeeHistory, Long> {

    @Transactional(readOnly = true)
    @Query("""
            SELECT fh FROM FeeHistory fh
            JOIN FETCH fh.feeMethod
            WHERE fh.feeMethod.competentAuthority = :competentAuthority
            AND fh.feeMethod.requestType IN :requestTypes
            ORDER BY fh.createdAt DESC
            """)
    Page<FeeHistory> findByCompetentAuthorityAndRequestTypes(
            CompetentAuthorityEnum competentAuthority,
            List<RequestType> requestTypes,
            Pageable pageable);
}
