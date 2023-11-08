package uk.gov.pmrv.api.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The Account Repository.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {

    @Transactional(readOnly = true)
    List<Account> findAllByIdIn(List<Long> ids);

    @Transactional(readOnly = true)
    List<Account> findAllByIdInAndAccountType(List<Long> ids, AccountType accountType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_WITH_LE_BY_ID_IN)
    List<Account> findAccountsWithLeByIdIn(List<Long> ids);

    @Transactional(readOnly = true)
    long countByLegalEntityId(Long legalEntityId);

    @Transactional(readOnly = true)
    //First issue: When the results exceed the page size Spring creates a count query to return the total number of the results. For the specific query below the count query produced is:
    //select count(acc) from uk.gov.pmrv.api.account.domain.Account acc left join acc.contacts contacts on contacts.{index} = :contactType where acc.verificationBodyId = :vbId and acc.accountType = :accountType
    //which is wrong and org.hibernate.query.sqm.ParsingException is thrown. By overriding the countQuery the application works as expected.
    //https://github.com/spring-projects/spring-data-jpa/issues/3133 has been opened for that.
    //Second issue: When a namedQuery is used the sql (and the countQuery) are produced at runtime and in a wrong way as mentioned above.
    //When the Query is inline, as done here, the queries (together with countQuery) are evaluated during deployment and produced correctly.
    @Query(value = "select new uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO(acc.id, acc.name, acc.emissionTradingScheme, VALUE(contacts)) "
            + "from Account acc "
            + "left join acc.contacts contacts on KEY(contacts) = :contactType "
            + "where acc.verificationBodyId = :vbId "
            + "and acc.accountType = :accountType "
            + "order by acc.name")
    Page<AccountContactVbInfoDTO> findAccountContactsByAccountTypeAndVbAndContactType(
            Pageable pageable, AccountType accountType, Long vbId, AccountContactType contactType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE)
    List<AccountContactInfoDTO> findAccountContactsByAccountIdsAndContactType(
            List<Long> accountIds, AccountContactType contactType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID)
    List<Account> findAccountsByContactTypeAndUserId(
            AccountContactType contactType, String userId);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_IDS_BY_ACCOUNT_TYPE_AND_VB)
    List<Long> findAllIdsByAccountTypeAndVB(AccountType accountType, Long vbId);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_WITH_CONTACTS_BY_VB_IN_LIST)
    Set<Account> findAllByVerificationWithContactsBodyIn(Set<Long> vbIds);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_WITH_CONTACTS_IN_LIST)
    Set<Account> findAllByVerificationBodyAndEmissionTradingSchemeWithContactsIn(Long vbId, Set<EmissionTradingScheme> emissionTradingSchemes);

    @Transactional(readOnly = true)
    Optional<Account> findByMigratedAccountId(String migratedAccountId);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.contacts WHERE a.migratedAccountId = :migratedAccountId")
    Account findByMigratedAccount(@Param("migratedAccountId") String migratedAccountId);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.location LEFT JOIN FETCH a.legalEntity le LEFT JOIN FETCH le.location WHERE a.migratedAccountId is not null")
    List<Account> findByMigratedAccountIdIsNotNull();
    
    @Transactional(readOnly = true)
    @Query("select acc.id "
        + "from Account acc "
        + "where acc.verificationBodyId = :vbId")
    List<Long> findAllIdsByVerificationBody(Long vbId);
}
