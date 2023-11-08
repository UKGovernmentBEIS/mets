package uk.gov.pmrv.api.mireport.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.mireport.common.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.List;

@Repository
public interface MiReportRepository extends JpaRepository<MiReportEntity, Long> {

    @Transactional(readOnly = true)
    List<MiReportSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority, AccountType accountType);
}
