package uk.gov.pmrv.api.mireport.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

@Repository
public interface PmrvMiReportRepository extends JpaRepository<PmrvMiReportEntity, Integer> {

    @Transactional(readOnly = true)
    List<MiReportSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority, AccountType accountType);
}
