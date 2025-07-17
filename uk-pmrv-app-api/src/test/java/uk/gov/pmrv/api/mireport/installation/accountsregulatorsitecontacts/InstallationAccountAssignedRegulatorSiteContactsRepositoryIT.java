package uk.gov.pmrv.api.mireport.installation.accountsregulatorsitecontacts;

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
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, InstallationAccountAssignedRegulatorSiteContactsRepository.class})
class InstallationAccountAssignedRegulatorSiteContactsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private InstallationAccountAssignedRegulatorSiteContactsRepository accountAssignedRegulatorSiteContactsRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAccountAssignedRegulatorSiteContacts() {
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        String userId3 = UUID.randomUUID().toString();
        Map<AccountContactType, String> contacts = Map.of(AccountContactType.PRIMARY, userId1, AccountContactType.CA_SITE, userId2);
        InstallationAccount account1 = createAccount(1L, "accountName1", AccountType.INSTALLATION,
            InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity, contacts);
        Authority authority1 = createAuthority(userId2,"code1", AuthorityStatus.ACTIVE, CompetentAuthorityEnum.ENGLAND);

        InstallationAccount account2 = createAccount(2L, "accountName2", AccountType.INSTALLATION,
            InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity, new HashMap<>());
        createAuthority(userId3, "code2", AuthorityStatus.ACTIVE, CompetentAuthorityEnum.ENGLAND);

        List<InstallationAccountAssignedRegulatorSiteContact> assignedRegulatorSiteContacts =
                accountAssignedRegulatorSiteContactsRepository.findAccountAssignedRegulatorSiteContacts(entityManager);

        assertEquals(2, assignedRegulatorSiteContacts.size());
        makeAssertions(account1, assignedRegulatorSiteContacts.get(0));
        makeAssertions(account2,  assignedRegulatorSiteContacts.get(1));
        assertEquals(authority1.getStatus().name(), assignedRegulatorSiteContacts.get(0).getAuthorityStatus());
        assertNull(assignedRegulatorSiteContacts.get(1).getAuthorityStatus());
        assertEquals(contacts.get(AccountContactType.CA_SITE), assignedRegulatorSiteContacts.get(0).getUserId());
        assertNull(assignedRegulatorSiteContacts.get(1).getUserId());


    }

    private void makeAssertions(InstallationAccount account, InstallationAccountAssignedRegulatorSiteContact accountAssignedRegulatorSiteContact) {

        assertEquals(account.getEmitterId(), accountAssignedRegulatorSiteContact.getAccountId());
        assertEquals(account.getName(), accountAssignedRegulatorSiteContact.getAccountName());
        assertEquals(account.getAccountType().name(), accountAssignedRegulatorSiteContact.getAccountType());
        assertEquals(account.getStatus().name(), accountAssignedRegulatorSiteContact.getAccountStatus());
        assertNotNull(account.getLegalEntity());
        assertEquals(account.getLegalEntity().getName(), accountAssignedRegulatorSiteContact.getLegalEntityName());
        assertNull(accountAssignedRegulatorSiteContact.getAssignedRegulatorName());
    }

    private LegalEntity createLegalEntity(String name) {
        LegalEntity le = LegalEntity.builder()
                .location(getLocation())
                .name(name)
                .status(LegalEntityStatus.ACTIVE)
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);
        return le;
    }

    private InstallationAccount createAccount(Long id, String name, AccountType type, InstallationAccountStatus status,
                                  CompetentAuthorityEnum competentAuthority, LegalEntity le, Map<AccountContactType, String> contacts) {

        InstallationAccount account = InstallationAccount.builder()
                .id(id)
                .name(name)
                .status(status)
                .accountType(type)
                .applicationType(ApplicationType.NEW_PERMIT)
                .siteName(name)
                .competentAuthority(competentAuthority)
                .legalEntity(le)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .commencementDate(LocalDate.of(2022, 1, 1))
                .emitterId("EM" + String.format("%05d", id))
                .location(getLocation())
                .contacts(contacts)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterType(EmitterType.GHGE)
                .build();

        entityManager.persist(account);
        return account;
    }

    private Authority createAuthority(String userId, String code, AuthorityStatus authorityStatus, CompetentAuthorityEnum competentAuthority) {
        Authority authority = Authority.builder()
                .userId(userId)
                .code(code)
                .status(authorityStatus)
                .createdBy("createdBy")
                .competentAuthority(competentAuthority)
                .build();
        entityManager.persist(authority);
        return authority;
    }

    private LocationOnShore getLocation() {
        return LocationOnShore.builder()
                .address(Address.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .build())
                .build();
    }
}
