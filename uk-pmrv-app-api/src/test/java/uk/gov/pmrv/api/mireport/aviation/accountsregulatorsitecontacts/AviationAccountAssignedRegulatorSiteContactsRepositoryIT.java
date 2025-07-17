package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

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
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, AviationAccountAssignedRegulatorSiteContactsRepository.class})
class AviationAccountAssignedRegulatorSiteContactsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationAccountAssignedRegulatorSiteContactsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAccountAssignedRegulatorSiteContacts() {
        String leName = "Wales Authority";
        String userId1 = "userId1";
        String userId2 = "userId2";

        LegalEntity legalEntity = LegalEntity.builder()
            .location(LocationOnShore.builder()
                .address(Address.builder()
                    .city("city")
                    .country("WL")
                    .line1("line")
                    .postcode("WL-18995")
                    .build())
                .build())
            .name(leName)
            .status(LegalEntityStatus.ACTIVE)
            .type(LegalEntityType.LIMITED_COMPANY)
            .build();
        entityManager.persist(legalEntity);

        Authority user1Authority = Authority.builder()
            .userId(userId1)
            .status(AuthorityStatus.ACTIVE)
            .createdBy("createdBy")
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .build();
        entityManager.persist(user1Authority);

        AviationAccount account1 = createAccount(1L, "accountName1", AviationAccountStatus.LIVE, legalEntity, Map.of(AccountContactType.CA_SITE, userId1));
        entityManager.persist(account1);
        AviationAccount account2 = createAccount(2L, "accountName2", AviationAccountStatus.LIVE, null, Map.of(AccountContactType.CA_SITE, userId2));
        entityManager.persist(account2);
        AviationAccount account3 = createAccount(3L, "accountName3", AviationAccountStatus.LIVE, legalEntity, Map.of(AccountContactType.PRIMARY, "primaryContactUser"));
        entityManager.persist(account3);
        AviationAccount account4 = createAccount(4L, "accountName4", AviationAccountStatus.CLOSED, legalEntity, Map.of(AccountContactType.CA_SITE, userId1));
        entityManager.persist(account4);

        flushAndClear();

        List<AviationAccountAssignedRegulatorSiteContact> expectedAccountAssignedRegulatorSiteContacts = List.of(
            createAccountAssignedRegulatorSiteContact(account4,  user1Authority.getStatus()),
            createAccountAssignedRegulatorSiteContact(account1,  user1Authority.getStatus()),
            createAccountAssignedRegulatorSiteContact(account2,  null),
            createAccountAssignedRegulatorSiteContact(account3,  null)
        );

        List<AviationAccountAssignedRegulatorSiteContact> accountAssignedRegulatorSiteContacts =
            repository.findAccountAssignedRegulatorSiteContacts(entityManager);

        assertEquals(4, accountAssignedRegulatorSiteContacts.size());
        assertThat(accountAssignedRegulatorSiteContacts).containsExactlyElementsOf(expectedAccountAssignedRegulatorSiteContacts);
    }

    private AviationAccount createAccount(Long id, String name, AviationAccountStatus status, LegalEntity le, Map<AccountContactType, String> contacts) {
        return AviationAccount.builder()
            .id(id)
            .name(name)
            .status(status)
            .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .legalEntity(le)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .commencementDate(LocalDate.of(2022, 1, 1))
            .emitterId("EM" + String.format("%05d", id))
            .contacts(contacts)
            .crcoCode(String.valueOf(id))
            .build();
    }

    private AviationAccountAssignedRegulatorSiteContact createAccountAssignedRegulatorSiteContact(AviationAccount account, AuthorityStatus authorityStatus) {
        Map<AccountContactType, String> contacts = account.getContacts();
        LegalEntity legalEntity = account.getLegalEntity();

        return AviationAccountAssignedRegulatorSiteContact.builder()
            .accountId(account.getEmitterId())
            .accountName(account.getName())
            .accountType(AccountType.AVIATION.name())
            .accountStatus(account.getStatus().getName())
            .legalEntityName(legalEntity != null ? legalEntity.getName() : null)
            .authorityStatus(authorityStatus != null ? authorityStatus.name() : null)
            .userId(!contacts.isEmpty() ? contacts.get(AccountContactType.CA_SITE) : null )
            .crcoCode(account.getCrcoCode())
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}