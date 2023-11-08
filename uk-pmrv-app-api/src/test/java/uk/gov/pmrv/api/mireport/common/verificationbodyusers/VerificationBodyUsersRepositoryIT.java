package uk.gov.pmrv.api.mireport.common.verificationbodyusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, VerificationBodyUsersRepository.class})
class VerificationBodyUsersRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private VerificationBodyUsersRepository verificationBodyUsersRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllVerificationBodyUsers() {

        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        String verifierAdmin = "verifier_admin";
        String verifier = "verifier";
        Role verifierAdminRole = createRole( "Verifier admin", verifierAdmin, RoleType.VERIFIER);
        Role verifierRole = createRole( "Verifier", verifier, RoleType.VERIFIER);
        Set<EmissionTradingScheme> ukEtsInstallations = Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS);
        Set<EmissionTradingScheme> ukEtsAviation = Set.of(EmissionTradingScheme.UK_ETS_AVIATION);
        VerificationBody verificationBody1 = createVerificationBody( "Verification Body Name",
                VerificationBodyStatus.ACTIVE, "accreditationNumber", ukEtsInstallations);
        VerificationBody verificationBody2 = createVerificationBody( "Verification Body Name 2",
                VerificationBodyStatus.ACTIVE, "accreditationNumber2", ukEtsAviation);
        Authority authority1 = createAuthority(verificationBody1.getId(), userId1, AuthorityStatus.ACTIVE,
                verifierAdmin, "createdBy");
        Authority authority2 = createAuthority(verificationBody2.getId(), userId2, AuthorityStatus.ACTIVE,
                verifier, "createdBy");


        List<VerificationBodyUser> verificationBodyUsers = verificationBodyUsersRepository.findAllVerificationBodyUsers(entityManager);

        assertEquals(2, verificationBodyUsers.size());
        makeAssertions(verificationBody1, authority1, verifierAdminRole, verificationBodyUsers.get(0));
        makeAssertions(verificationBody2, authority2, verifierRole, verificationBodyUsers.get(1));
        assertTrue(verificationBodyUsers.get(0).getIsAccreditedForUKETSInstallations());
        assertFalse(verificationBodyUsers.get(0).getIsAccreditedForEUETSInstallations());
        assertFalse(verificationBodyUsers.get(0).getIsAccreditedForUKETSAviation());
        assertFalse(verificationBodyUsers.get(0).getIsAccreditedForCorsia());
        assertFalse(verificationBodyUsers.get(1).getIsAccreditedForUKETSInstallations());
        assertFalse(verificationBodyUsers.get(1).getIsAccreditedForEUETSInstallations());
        assertTrue(verificationBodyUsers.get(1).getIsAccreditedForUKETSAviation());
        assertFalse(verificationBodyUsers.get(1).getIsAccreditedForCorsia());
    }

    private void makeAssertions(VerificationBody verificationBody, Authority authority, Role role, VerificationBodyUser verificationBodyUser) {

        assertEquals(verificationBody.getName(), verificationBodyUser.getVerificationBodyName());
        assertEquals(verificationBody.getStatus().name(), verificationBodyUser.getAccountStatus());
        assertEquals(verificationBody.getAccreditationReferenceNumber(), verificationBodyUser.getAccreditationReferenceNumber());
        assertEquals(authority.getStatus().name(),verificationBodyUser.getAuthorityStatus());
        assertEquals(role.getName(),verificationBodyUser.getRole());
        assertEquals(authority.getUserId(), verificationBodyUser.getUserId());
    }


    private VerificationBody createVerificationBody(String name, VerificationBodyStatus status,
                                                    String accreditationReferenceNumber, Set<EmissionTradingScheme> emissionTradingSchemes) {

        VerificationBody verificationBody = VerificationBody.builder()
                .name(name)
                .status(status)
                .address(createAddress())
                .createdDate(LocalDateTime.now())
                .accreditationReferenceNumber(accreditationReferenceNumber)
                .emissionTradingSchemes(emissionTradingSchemes)
                .build();

        entityManager.persist(verificationBody);
        return verificationBody;
    }

    private Authority createAuthority( Long verificationBodyId, String userId, AuthorityStatus status,
                                      String code, String createdBy) {

        Authority authority = Authority.builder()
                .verificationBodyId(verificationBodyId)
                .userId(userId)
                .status(status)
                .code(code)
                .createdBy(createdBy)
                .creationDate(LocalDateTime.now())
                .build();

        entityManager.persist(authority);
        return authority;
    }

    private Role createRole(String name, String code, RoleType type) {
        Role role = Role.builder()
                .name(name)
                .code(code)
                .type(type)
                .build();

        entityManager.persist(role);
        return role;
    }

    private Address createAddress() {

        return Address.builder()
                .country("GR")
                .city("Athens")
                .postcode("postcode")
                .line1("line 1")
                .build();
    }

}
