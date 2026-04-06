package uk.gov.pmrv.api.mireport.system.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

@Repository
public interface PmrvMiReportSystemRepository extends JpaRepository<PmrvMiReportSystemEntity, Integer> {

    @Transactional(readOnly = true)
    List<MiReportSystemSearchResult> findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum competentAuthority, AccountType accountType);
    
    @Transactional(readOnly = true)
	boolean existsByCompetentAuthorityAndAccountTypeAndMiReportType(CompetentAuthorityEnum competentAuthority,
			AccountType accountType, String miReportType);
}
