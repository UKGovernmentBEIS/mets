package uk.gov.pmrv.api.account.aviation.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

public interface AviationAccountCustomRepository {

    @Transactional(readOnly = true)
    AviationAccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria);

    @Transactional(readOnly = true)
    AviationAccountSearchResults findByCompAuth(CompetentAuthorityEnum compAuth, AccountSearchCriteria searchCriteria);
}
