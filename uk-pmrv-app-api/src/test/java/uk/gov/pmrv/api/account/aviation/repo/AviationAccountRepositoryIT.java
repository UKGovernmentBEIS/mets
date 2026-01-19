package uk.gov.pmrv.api.account.aviation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByIdAndStatusNotIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        AviationAccount account1 = createAccount(accountId1, "account1", AviationAccountStatus.LIVE,
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.CORSIA);
        createAccount(accountId2, "account2", AviationAccountStatus.NEW,
            CompetentAuthorityEnum.ENGLAND, 2L, EmissionTradingScheme.UK_ETS_AVIATION);

        flushAndClear();

        Optional<AviationAccount> result = repository.findByIdAndStatusNotIn(accountId1, List.of(AviationAccountStatus.NEW));

        assertThat(result).isNotEmpty();
        assertEquals(account1, result.get());
    }

    @Test
    void findAccountContactsByCaAndContactTypeAndStatusNotIn() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        AccountContactType serviceContactType = AccountContactType.SERVICE;

        AviationAccount account1 = createAccount(1L, "account1", AviationAccountStatus.LIVE, 
            competentAuthority, 1L, EmissionTradingScheme.UK_ETS_AVIATION);
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        account1.getContacts().put(AccountContactType.SERVICE, "service1");
        repository.save(account1);

        AviationAccount account2 = createAccount(2L, "account2", AviationAccountStatus.CLOSED, 
            competentAuthority, 2L, EmissionTradingScheme.CORSIA);
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        account2.getContacts().put(AccountContactType.SERVICE, "service2");
        repository.save(account2);

        AviationAccount account3 = createAccount(3L, "account3", AviationAccountStatus.LIVE, 
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.CORSIA);
        account3.getContacts().put(AccountContactType.PRIMARY, "primary3");
        account3.getContacts().put(AccountContactType.SERVICE, "service3");
        repository.save(account3);

        AviationAccount account4 = createAccount(4L, "account4", AviationAccountStatus.NEW, 
            competentAuthority, 2L, EmissionTradingScheme.CORSIA);
        account4.getContacts().put(AccountContactType.PRIMARY, "primary4");
        repository.save(account4);

        List<AccountContactInfoDTO> expectedAccountContactInfos = List.of(
            AccountContactInfoDTO.builder().accountId(1L).accountName("account1").userId("service1").build(),
            AccountContactInfoDTO.builder().accountId(4L).accountName("account4").build()
        );

        flushAndClear();

        Page<AccountContactInfoDTO> result = repository.findAccountContactsByCaAndContactTypeAndStatusNotIn(PageRequest.of(0, 5), competentAuthority,
            serviceContactType, List.of(AviationAccountStatus.CLOSED));

        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAccountContactInfos);
    }

    @Test
    void findAccountIdsByCaAndStatusNotIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;
        Long accountId4 = 4L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;

        createAccount(accountId1, "account1", AviationAccountStatus.LIVE, 
            competentAuthority, 1L, EmissionTradingScheme.CORSIA);
        createAccount(accountId2, "account2", AviationAccountStatus.NEW, 
            competentAuthority, 2L, EmissionTradingScheme.UK_ETS_AVIATION);
        createAccount(accountId3, "account3", AviationAccountStatus.LIVE, 
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.CORSIA);
        createAccount(accountId4, "account4", AviationAccountStatus.CLOSED, 
            competentAuthority, 2L, EmissionTradingScheme.UK_ETS_AVIATION);

        flushAndClear();

        List<Long> result = repository.findAccountIdsByCaAndStatusNotIn(competentAuthority, List.of(AviationAccountStatus.CLOSED));

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(accountId1, accountId2);
    }

    @Test
    void findAllByStatusIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;

        createAccount(accountId1, "account1", AviationAccountStatus.LIVE, 
            competentAuthority, 1L, EmissionTradingScheme.CORSIA);
        createAccount(accountId2, "account2", AviationAccountStatus.NEW, 
            competentAuthority, 2L, EmissionTradingScheme.UK_ETS_AVIATION);
        createAccount(accountId3, "account3", AviationAccountStatus.CLOSED, 
            CompetentAuthorityEnum.ENGLAND, 1L, EmissionTradingScheme.CORSIA);

        flushAndClear();

        List<AviationAccount> result = repository.findAllByStatusIn( List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE));

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting(Account::getId).containsExactlyInAnyOrder(accountId1, accountId2);
    }

    
	private AviationAccount createAccount(Long id, String accountName, AviationAccountStatus accountStatus,
			CompetentAuthorityEnum ca, Long verificationBodyId, EmissionTradingScheme ets) {
        AviationAccount account = AviationAccount.builder()
            .id(id)
            .accountType(AccountType.AVIATION)
            .commencementDate(LocalDate.now())
            .competentAuthority(ca)
            .verificationBodyId(verificationBodyId)
            .status(accountStatus)
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
            .emissionTradingScheme(ets)
            .emitterId("EM" + String.format("%05d", id))
            .crcoCode(String.valueOf(id))
            .build();

        entityManager.persist(account);
        return account;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
