package uk.gov.pmrv.api.account.aviation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.impl.AviationAccountCustomRepositoryImpl;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationAccountCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationAccountCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByAccountIds_by_account_ids() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.NEW);
        AviationAccount account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());

        AviationAccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }

    @Test
    void findByAccountIds_by_account_ids_with_term() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.CLOSED);
        AviationAccount account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);
        createAccount(4L, "account4", CompetentAuthorityEnum.ENGLAND, "crcoCode4", 1L, AviationAccountStatus.NEW);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("crcoCode")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());

        AviationAccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }

    @Test
    void findByAccountIds_by_account_ids_search_key_filter() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.CLOSED);
        AviationAccount account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("crcoCode1")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());

        AviationAccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getId).containsOnly(account1.getId());
    }

    @Test
    void findByAccountIds_by_account_ids_search_key_not_present() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.CLOSED);
        AviationAccount account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("notpresentkey")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());

        AviationAccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isZero();
    }

    @Test
    void findByAccountIds_paging() {
        int totalAccounts = 35;
        for (int i = 1; i <= totalAccounts; i++) {
            createAccount((long) i, "account" + i, CompetentAuthorityEnum.ENGLAND, "crcoCode" + i, 1L, AviationAccountStatus.LIVE);
        }

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("account")
                .paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build()).build();
        List<Long> accountIds = LongStream.rangeClosed(1L, totalAccounts).boxed().collect(Collectors.toList());

        List<Long> expectedAccountIds = LongStream.rangeClosed(11L, 20L).boxed().collect(Collectors.toList());

        AviationAccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(totalAccounts);
        assertThat(results.getAccounts().size()).isEqualTo(10);
        assertTrue(results.getAccounts().stream().map(AviationAccountSearchResultsInfoDTO::getId).allMatch(expectedAccountIds::contains));
    }

    @Test
    void findByCompAuth() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.CLOSED);
        AviationAccount account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder().paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();

        AviationAccountSearchResults results = repo.findByCompAuth(CompetentAuthorityEnum.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }

    @Test
    void findByCompAuth_with_term() {
        AviationAccount account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, "crcoCode1", 1L, AviationAccountStatus.LIVE);
        createAccount(2L, "account2", CompetentAuthorityEnum.WALES, "crcoCode2", 1L, AviationAccountStatus.CLOSED);
        createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, "crcoCode3", 1L, AviationAccountStatus.LIVE);

        flushAndClear();

        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("crcoCode1")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();

        AviationAccountSearchResults results = repo.findByCompAuth(CompetentAuthorityEnum.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getId).containsOnly(account1.getId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId());
        assertThat(results.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus());
    }

    private AviationAccount createAccount(Long id, String accountName, CompetentAuthorityEnum ca, String crcoCode,
                                          Long verificationBodyId, AviationAccountStatus status) {

        AviationAccount account = AviationAccount.builder()
                .id(id)
                .accountType(AccountType.AVIATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(ca)
                .verificationBodyId(verificationBodyId)
                .status(status)
                .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
                .location(
                        LocationOnShore.builder()
                                .gridReference("grid")
                                .address(
                                        Address.builder()
                                                .city("city")
                                                .country("GR")
                                                .line1("line")
                                                .postcode("postcode")
                                                .build())
                                .build())
                .name(accountName)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", id))
                .crcoCode(crcoCode)
                .build();
        entityManager.persist(account);
        return account;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
