package uk.gov.pmrv.api.mireport.aviation.accountuserscontacts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, AviationAccountUsersContactsRepository.class})
class AviationAccountUsersContactsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationAccountUsersContactsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAccountUserContacts() {
        Long accountId = 1L;
        String userId1 = "user1";
        String userId2 = "user2";
        String operatorRoleCode = "code";
        String roleName = "rolename";

        Role role = Role.builder()
            .code(operatorRoleCode)
            .name(roleName)
            .type(RoleType.OPERATOR)
            .build();
        entityManager.persist(role);

        Authority user1Authority = Authority.builder()
            .accountId(1L)
            .code(operatorRoleCode)
            .userId(userId1)
            .status(AuthorityStatus.ACTIVE)
            .createdBy(userId1)
            .build();
        entityManager.persist(user1Authority);

        Authority user2Authority = Authority.builder()
            .accountId(1L)
            .code(operatorRoleCode)
            .userId(userId2)
            .status(AuthorityStatus.ACCEPTED)
            .createdBy(userId2)
            .build();
        entityManager.persist(user2Authority);

        Map<AccountContactType, String> contacts = Map.of(
            AccountContactType.PRIMARY, userId1,
            AccountContactType.SERVICE, userId2,
            AccountContactType.FINANCIAL, userId1,
            AccountContactType.SECONDARY, userId2
        );
        AviationAccount account = AviationAccount.builder()
            .id(accountId)
            .name("name")
            .status(AviationAccountStatus.LIVE)
            .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .commencementDate(LocalDate.of(2022, 1, 1))
            .emitterId("EM" + String.format("%05d", accountId))
            .contacts(contacts)
            .crcoCode(String.valueOf(accountId))
            .build();
        entityManager.persist(account);

        flushAndClear();

        List<AccountUserContact> expectedAccountUserContacts = List.of(
            createAccountUserContact(account,  userId1, user1Authority.getStatus(), roleName),
            createAccountUserContact(account,  userId2, user2Authority.getStatus(), roleName)
        );


        //invoke
        List<AccountUserContact> accountUserContacts = repository.findAccountUserContacts(entityManager);

        assertEquals(2, accountUserContacts.size());
        assertThat(accountUserContacts).containsExactlyInAnyOrderElementsOf(expectedAccountUserContacts);

    }

    private AccountUserContact createAccountUserContact(AviationAccount account, String userId, AuthorityStatus authorityStatus, String roleName ) {
        Map<AccountContactType, String> contacts = account.getContacts();

        return AccountUserContact.builder()
            .userId(userId)
            .accountType(account.getAccountType().name())
            .accountId(account.getEmitterId())
            .accountName(account.getName())
            .accountStatus(account.getStatus().name())
            .primaryContact(userId.equals(contacts.get(AccountContactType.PRIMARY)))
            .secondaryContact(userId.equals(contacts.get(AccountContactType.SECONDARY)))
            .financialContact(userId.equals(contacts.get(AccountContactType.FINANCIAL)))
            .serviceContact(userId.equals(contacts.get(AccountContactType.SERVICE)))
            .authorityStatus(authorityStatus.name())
            .role(roleName)
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}