package uk.gov.pmrv.api.account.installation.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

public interface InstallationAccountCustomRepository {

    @Transactional(readOnly = true)
    AccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria);
    
    @Transactional(readOnly = true)
    AccountSearchResults findByCompAuth(CompetentAuthorityEnum compAuth, AccountSearchCriteria searchCriteria);
}
