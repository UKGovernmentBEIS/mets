package uk.gov.pmrv.api.mireport.installation.accountuserscontacts;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest(properties = { "spring.jpa.properties.jakarta.persistence.validation.mode=none",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
@Import({ObjectMapper.class, InstallationAccountUsersContactsRepository.class})
class InstallationAccountUsersContactsRepositoryIT extends AbstractContainerBaseTest {
    @Autowired
    private InstallationAccountUsersContactsRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAccountUserContacts() {
        Long accountId = 1L;
        String userId = "user1";
        Map<AccountContactType, String> contacts = Map.of(AccountContactType.PRIMARY, userId, AccountContactType.FINANCIAL, userId);
        InstallationAccount account = createAccount(accountId, CompetentAuthorityEnum.ENGLAND, contacts);
        Authority authority = createAuthority(userId);
        Role role = createRole();
        PermitEntity permit = createPermit("permitId", accountId);
        flushAndClear();
        InstallationAccountUserContact accountUserContactExpected = InstallationAccountUserContact
                .builder()
                .userId(userId)
                .accountType(account.getAccountType().name())
                .accountId(account.getEmitterId())
                .accountName(account.getName())
                .accountStatus(account.getStatus().name())
                .permitId(permit.getId())
                .permitType(permit.getPermitContainer().getPermitType().name())
                .legalEntityName(account.getLegalEntity().getName())
                .primaryContact(true)
                .secondaryContact(false)
                .financialContact(true)
                .serviceContact(false)
                .authorityStatus(authority.getStatus().name())
                .role(role.getName())
                .build();

        //invoke
        List<InstallationAccountUserContact> result = repo.findAccountUserContacts(entityManager);

        //verify
        assertEquals(1, result.size());
        assertEquals(accountUserContactExpected, result.get(0));
    }

    private PermitEntity createPermit(String id, Long accountId) {
        PermitEntity permit = PermitEntity.builder()
                .id(id)
                .accountId(accountId)
                .permitContainer(PermitContainer.builder()
                        .permitType(PermitType.GHGE)
                        .permit(new Permit())
                        .installationOperatorDetails(new InstallationOperatorDetails())
                        .build())
                .build();
        entityManager.persist(permit);
        return permit;
    }

    private Role createRole() {
        Role role = Role.builder()
                .code("code")
                .name("roleName")
                .type(RoleTypeConstants.OPERATOR)
                .build();
        entityManager.persist(role);
        return role;
    }

    private Authority createAuthority(String userId) {
        Authority authority = Authority.builder()
                .accountId(1L)
                .userId(userId)
                .code("code")
                .status(AuthorityStatus.ACTIVE)
                .createdBy("createdBy")
                .build();
        entityManager.persist(authority);
        return authority;
    }

    private InstallationAccount createAccount(Long id, CompetentAuthorityEnum ca, Map<AccountContactType, String> contacts) {
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
                .name("leName")
                .status(LegalEntityStatus.ACTIVE)
                .referenceNumber("regNumber")
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);
        InstallationAccount account = InstallationAccount.builder()
                .id(id)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(ca)
                .status(InstallationAccountStatus.LIVE)
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
                .name("accountName")
                .siteName("siteName")
                .emitterId("EM" + String.format("%05d", id))
                .contacts(contacts)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();
        entityManager.persist(account);
        return account;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}