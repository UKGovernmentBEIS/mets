package uk.gov.pmrv.api.account.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.domain.Address;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public abstract class AbstractLegalEntityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private LegalEntityRepository repo;

    @Autowired
    private EntityManager entityManager;

    public abstract Account buildAccount(Long id, String accountName, LegalEntity legalEntity);

    @Test
    void findActiveLegalEntitiesByAccounts() {
        final String le1Name = "le1";
        Account account1Active = createAccount(1L, "account1", le1Name, LegalEntityStatus.ACTIVE);

        final String le2Name = "le1_pending";
        Account account2Pending = createAccount(2L, "account1_pending", le2Name, LegalEntityStatus.PENDING);

        createAccount(3L, "account3", "le3", LegalEntityStatus.ACTIVE);

        flushAndClear();

        List<LegalEntity> list = repo.findActiveLegalEntitiesByAccountsOrderByName(Set.of(account1Active.getId(), account2Pending.getId()));
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo(le1Name);
        assertThat(list.get(0).getHoldingCompany()).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(createHoldingCompany());
    }

    @Test
    void existsLegalEntityInAnyOfAccounts_false() {
        Account account = createAccount(1L, "account1", "le", LegalEntityStatus.ACTIVE);

        flushAndClear();

        boolean result = repo.existsLegalEntityInAnyOfAccounts(account.getLegalEntity().getId(), Set.of(-1L));
        assertThat(result).isFalse();
    }

    @Test
    void existsLegalEntityInAnyOfAccounts_true() {
        Account account = createAccount(1L, "account1", "le", LegalEntityStatus.ACTIVE);

        flushAndClear();

        boolean result = repo.existsLegalEntityInAnyOfAccounts(account.getLegalEntity().getId(), Set.of(-1L, account.getId()));
        assertThat(result).isTrue();
    }

    @Test
    void existsActiveLegalEntityNameInAnyOfAccounts_false() {
        final String leName = "le";
        createAccount(1L, "account1", leName, LegalEntityStatus.PENDING);

        flushAndClear();

        boolean result = repo.existsActiveLegalEntityNameInAnyOfAccounts(leName, Set.of(-1L));
        assertThat(result).isFalse();
    }

    @Test
    void existsActiveLegalEntityNameInAnyOfAccounts_true() {
        final String leName = "le1";

        Account account = createAccount(1L, "account1", leName, LegalEntityStatus.ACTIVE);

        flushAndClear();

        boolean result = repo.existsActiveLegalEntityNameInAnyOfAccounts(leName, Set.of(-1L, account.getId()));
        assertThat(result).isTrue();
    }

    private Account createAccount(Long id, String accountName, String leName, LegalEntityStatus leStatus) {
        LegalEntity le = createLegalEntity(leName, leStatus);

        Account account = buildAccount(id, accountName, le);
        entityManager.persist(account);
        return account;
    }

    private LegalEntity createLegalEntity(String name, LegalEntityStatus status) {
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
            .name(name)
            .status(status)
            .referenceNumber("regNumber")
            .type(LegalEntityType.LIMITED_COMPANY)
            .holdingCompany(createHoldingCompany())
            .build();
        entityManager.persist(le);
        return le;
    }

    private HoldingCompany createHoldingCompany() {
        return HoldingCompany.builder()
            .name("holding")
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
