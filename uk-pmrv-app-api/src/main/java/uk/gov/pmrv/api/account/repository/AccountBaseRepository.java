package uk.gov.pmrv.api.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AccountBaseRepository<T extends Account> extends JpaRepository<T, Long> {

    @Transactional(readOnly = true)
    @Query("select acc from #{#entityName} as acc where acc.id = :id and acc.status not in (:statuses)")
    Optional<T> findByIdAndStatusNotIn(Long id, List<? extends AccountStatus> statuses);

    @Transactional(readOnly = true)
    @Query("select new uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO(acc.id, acc.name, VALUE(contacts)) "
        + "from Account acc "
        + "join #{#entityName} child_acc on acc.id = child_acc.id "
        + "left join acc.contacts contacts on KEY(contacts) = :contactType "
        + "where acc.competentAuthority = :ca "
        + "and child_acc.status not in (:statuses) "
        + "and (:searchTerm is null "
        + "     or lower(acc.name) like :searchTerm escape '\\' "
        + "     or lower(acc.emitterId) like :searchTerm escape '\\' "
        + "     or exists (select 1 from AccountSearchAdditionalKeyword ak "
        + "                where ak.accountId = acc.id "
        + "                  and lower(ak.value) like :searchTerm escape '\\')) "
        + "order by acc.name")
    Page<AccountContactInfoDTO> findAccountContactsByCaAndContactTypeAndStatusNotIn(
        Pageable pageable, String searchTerm, CompetentAuthorityEnum ca, AccountContactType contactType, List<? extends AccountStatus> statuses);

    @Transactional(readOnly = true)
    @Query("select acc.id "
        + "from Account acc "
        + "join #{#entityName} child_acc on acc.id = child_acc.id "
        + "where acc.competentAuthority = :ca "
        + "and child_acc.status not in (:statuses) ")
    List<Long> findAccountIdsByCaAndStatusNotIn(CompetentAuthorityEnum ca, List<? extends AccountStatus> statuses);
}
