package uk.gov.pmrv.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.domain.RolePermission;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.core.domain.PmrvPermission;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class LazyInitializationIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RequestActionRepository requestActionRepository;

    @Test
    void testLazyInitialization_whenLazyOneToManyAccessedAfterSessionCloses_thenThrowException() {
        RolePermission rolePermission = RolePermission.builder()
                .permission(PmrvPermission.PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK)
                .build();
        entityManager.persist(rolePermission);

        final Role role = new Role();
        role.setName("roleName");
        role.setCode("roleCode");
        role.setType(RoleTypeConstants.OPERATOR);
        role.setRolePermissions(new ArrayList<>());
        role.addPermission(rolePermission);
        entityManager.persist(role);

        entityManager.flush();
        entityManager.clear();

        final List<Role> roles = roleRepository.findAll();

        TestTransaction.end();

        assertEquals(1, roles.size());
        assertThrows(LazyInitializationException.class, () -> roles.get(0).getRolePermissions().get(0).getRole());
    }

    @Test
    void testLazyInitialization_whenLazyManyToOneAccessedAfterSessionCloses_thenThrowException() {

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
                .name("leName1")
                .status(LegalEntityStatus.ACTIVE)
                .referenceNumber("regNumber")
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);

        Account account = InstallationAccount.builder()
                .id(1L)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .applicationType(ApplicationType.NEW_PERMIT)
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(1L)
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
                .name("account1")
                .siteName("account1")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM00001")
                .build();
        entityManager.persist(account);

        entityManager.flush();
        entityManager.clear();

        final List<Account> accounts = accountRepository.findAll();

        TestTransaction.end();

        assertEquals(1, accounts.size());
        assertThrows(LazyInitializationException.class, () -> accounts.get(0).getLegalEntity().getName());
    }

    @Test
    void testLazyInitialization_whenLazyBasicJsonAccessedAfterSessionCloses_thenThrowException() {
        Request request = Request.builder()
                .id("1")
                .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
                .status(RequestStatus.IN_PROGRESS)
                .creationDate(LocalDateTime.now())
                .build();

        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
                .submitterId("userId")
                .submitter("username")
                .creationDate(LocalDateTime.now())
                .payload(InstallationAccountOpeningApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)
                        .build())
                .build();

        request.setRequestActions(new ArrayList<>());
        request.addRequestAction(requestAction);

        entityManager.persist(request);
        entityManager.persist(requestAction);

        entityManager.flush();
        entityManager.clear();

        final List<RequestAction> requestActions = requestActionRepository.findAll();

        TestTransaction.end();

        assertEquals(1, requestActions.size());
        assertThrows(LazyInitializationException.class, () -> requestActions.get(0).getPayload());
    }
}