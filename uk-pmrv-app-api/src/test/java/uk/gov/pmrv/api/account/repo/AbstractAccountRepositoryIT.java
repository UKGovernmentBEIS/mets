package uk.gov.pmrv.api.account.repo;

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
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public abstract class AbstractAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountRepository repo;

    @Autowired
    private EntityManager entityManager;

    public abstract Account buildAccount(Long id, String accountName, CompetentAuthorityEnum ca, Long verificationBodyId,
                                         EmissionTradingScheme emissionTradingScheme, LegalEntity legalEntity, HoldingCompany holdingCompany);

    public abstract AccountType getAccounTtype();

    @Test
    void findAccountContactsByAccountIdsAndContactType() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, "leName1",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, "leName2",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.EU_ETS_INSTALLATIONS, buildHoldingCompany());
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, "leName3",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.EU_ETS_INSTALLATIONS, buildHoldingCompany());
        repo.save(account3);

        List<Long> accountIds = List.of(account1.getId(), account2.getId(), account3.getId());

        flushAndClear();

        //invoke
        List<AccountContactInfoDTO> result = repo.findAccountContactsByAccountIdsAndContactType(accountIds,
            AccountContactType.CA_SITE);

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountId()).isEqualTo(account1.getId());
    }

    @Test
    void findAccountContactsByAccountTypeAndVbAndContactType() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, "leName1",
                LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        account1.getContacts().put(AccountContactType.VB_SITE, "test1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, "leName2",
                LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        account1.getContacts().put(AccountContactType.VB_SITE, "test2");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, "leName3",
                LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        account1.getContacts().put(AccountContactType.VB_SITE, "test3");
        repo.save(account3);

        Page<AccountContactVbInfoDTO> page = repo.findAccountContactsByAccountTypeAndVbAndContactType(PageRequest.of(0, 1), getAccounTtype(), vbId, AccountContactType.VB_SITE);
        assertThat(page).hasSize(1);
    }

        @Test
    void findAccountsByContactTypeAndUserId() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, "leName1",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.EU_ETS_INSTALLATIONS, buildHoldingCompany());
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, "leName2",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        account2.getContacts().put(AccountContactType.PRIMARY, "test1");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, "leName3",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS,buildHoldingCompany());
        repo.save(account3);

        flushAndClear();

        List<Account> result = repo.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, "test1");

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(account1.getId());
    }

    @Test
    void findAllByVerificationBodyIn() {
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, 1L,"leName1",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.EU_ETS_INSTALLATIONS, buildHoldingCompany());
        createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, 2L, "leName2",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.EU_ETS_INSTALLATIONS, buildHoldingCompany());
        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, 3L, "leName3",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        Account account4 = createAccount(4L, "account4", CompetentAuthorityEnum.ENGLAND, 3L, "leName4",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());

        Set<Account> result = repo.findAllByVerificationWithContactsBodyIn(Set.of(1L, 3L));

        assertThat(result).extracting(Account::getId)
            .containsExactlyInAnyOrder(account1.getId(), account3.getId(), account4.getId());
    }

    @Test
    void findAllByVerificationBodyAndEmissionTradingSchemeIn() {
        Long vbId = 1L;
        Long anotherVbId = 2L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, "leName1",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());
        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, "leName2",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_AVIATION, buildHoldingCompany());
        createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, "leName3",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.CORSIA, buildHoldingCompany());
        createAccount(4L, "account4", CompetentAuthorityEnum.ENGLAND, anotherVbId, "leName4",
            LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS, buildHoldingCompany());

        Set<Account> result = repo.findAllByVerificationBodyAndEmissionTradingSchemeWithContactsIn(vbId,
            Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.UK_ETS_AVIATION));

        assertThat(result).extracting(Account::getId)
            .containsExactlyInAnyOrder(account1.getId(), account2.getId());
    }

    private Account createAccount(Long id, String accountName, CompetentAuthorityEnum ca, Long vbId,
                                  String leName, LegalEntityStatus leStatus,
                                  EmissionTradingScheme emissionTradingScheme, HoldingCompany holdingCompany) {
        LegalEntity legalEntity = createLegalEntity(leName, leStatus, holdingCompany);
        Account account = buildAccount(id, accountName, ca, vbId, emissionTradingScheme, legalEntity, holdingCompany);
        entityManager.persist(account);
        return account;
    }

    private LegalEntity createLegalEntity(String leName, LegalEntityStatus leStatus, HoldingCompany holdingCompany) {
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
            .holdingCompany(holdingCompany)
            .build();

        entityManager.persist(le);
        return le;
    }

    private static HoldingCompany buildHoldingCompany() {
        return HoldingCompany.builder()
            .name("Holding")
            .registrationNumber("123456")
            .address(HoldingCompanyAddress.builder()
                .line1("line1")
                .line2("line2")
                .city("city")
                .postcode("postcode")
                .build())
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
