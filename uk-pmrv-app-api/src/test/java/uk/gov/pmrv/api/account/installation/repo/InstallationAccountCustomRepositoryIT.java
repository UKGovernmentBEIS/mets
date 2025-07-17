package uk.gov.pmrv.api.account.installation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.repository.impl.InstallationAccountCustomRepositoryImpl;
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
class InstallationAccountCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private InstallationAccountCustomRepositoryImpl repo;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findByAccountIds_by_account_ids() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByAccountIds_by_account_ids_with_term() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
        createAccount(4L, "account4", "leName4", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("leName")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByAccountIds_by_account_ids_search_key_filter() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("leName1")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getId).containsOnly(account1.getId());
    }
    
    @Test
    void findByAccountIds_by_account_ids_search_key_not_present() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("notpresentkey")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isZero();
    }
    
    @Test
    void findByAccountIds_paging() {
        int totalAccounts = 35;
        for(int i = 1; i <= totalAccounts; i++) {
            createAccount((long) i, "account" + i, "leName" + i, CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        }

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("account")
                .paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build()).build();
        List<Long> accountIds = LongStream.rangeClosed(1L, totalAccounts).boxed().collect(Collectors.toList());
        
        List<Long> expectedAccountIds = LongStream.rangeClosed(11L, 20L).boxed().collect(Collectors.toList());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(totalAccounts);
        assertThat(results.getAccounts().size()).isEqualTo(10);
        assertTrue(results.getAccounts().stream().map(AccountSearchResultsInfoDTO::getId).allMatch(expectedAccountIds::contains));
    }
    
    @Test
    void findByCompAuth() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder().paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        
        AccountSearchResults results = repo.findByCompAuth(CompetentAuthorityEnum.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByCompAuth_with_term() {
        InstallationAccount account1 = createAccount(1L, "account1", "leName1", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthorityEnum.WALES, InstallationAccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        InstallationAccount account3 = createAccount(3L, "account3", "leName3", CompetentAuthorityEnum.ENGLAND, InstallationAccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
            .term("leName")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
        
        AccountSearchResults results = repo.findByCompAuth(CompetentAuthorityEnum.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountSearchResultsInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }

    private InstallationAccount createAccount(Long id, String accountName, String leName, CompetentAuthorityEnum ca,
            InstallationAccountStatus status, LegalEntityStatus leStatus) {
        return createAccount(id, accountName, leName, ca, null, status, leStatus);
    }

    private InstallationAccount createAccount(Long id, String accountName, String leName, CompetentAuthorityEnum ca,
            Long verificationBodyId, InstallationAccountStatus status, LegalEntityStatus leStatus) {
        LegalEntity le = LegalEntity.builder()
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
                .name(leName)
                .status(leStatus)
                .referenceNumber("regNumber")
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);
        InstallationAccount account = InstallationAccount.builder()
                .id(id)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .applicationType(ApplicationType.NEW_PERMIT)
                .commencementDate(LocalDate.now())
                .competentAuthority(ca)
                .verificationBodyId(verificationBodyId)
                .status(status)
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
                .siteName(accountName)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", id))
                .build();
        entityManager.persist(account);
        return account;
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
