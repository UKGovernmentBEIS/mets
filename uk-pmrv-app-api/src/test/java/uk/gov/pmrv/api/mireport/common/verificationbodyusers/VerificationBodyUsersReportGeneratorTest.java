package uk.gov.pmrv.api.mireport.common.verificationbodyusers;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyUsersReportGeneratorTest {

    @InjectMocks
    private VerificationBodyUsersReportGeneratorHandlerHandler generator;

    @Mock
    private VerificationBodyUsersRepository verificationBodyUsersRepository;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private EntityManager entityManager;

    @Test
    void getReportType() {
        assertEquals("LIST_OF_VERIFICATION_BODY_USERS", generator.getReportType());
    }

    @Test
    void generateMiReport() {

        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().build();
        LocalDateTime now = LocalDateTime.now();
        String userId = UUID.randomUUID().toString();
        VerifierUserInfoDTO verifierUserInfoDTO = createOperatorUserInfoDTO(userId, "First Name", "Last Name", "6954", "email", now);
        when(userAuthService.getUsersWithAttributes(Collections.singletonList(userId), VerifierUserInfoDTO.class))
                .thenReturn(List.of(verifierUserInfoDTO));
        when(verificationBodyUsersRepository.findAllVerificationBodyUsers(entityManager))
                .thenReturn(List.of(createVerificationBodyUser("Verification Body Name", InstallationAccountStatus.LIVE, "12345",
                        "Verifier admin", AuthorityStatus.ACTIVE,  userId)));

        VerificationBodyUsersMiReportResult miReportResult =
                (VerificationBodyUsersMiReportResult) generator.generateMiReport(entityManager, reportParams);

        assertNotNull(miReportResult);
        assertEquals("LIST_OF_VERIFICATION_BODY_USERS", miReportResult.getReportType());
        assertEquals(1, miReportResult.getResults().size());
        VerificationBodyUser verificationBodyUser = miReportResult.getResults().get(0);
        assertEquals(userId, verificationBodyUser.getUserId());
        assertEquals(verifierUserInfoDTO.getFullName(), verificationBodyUser.getVerifierFullName());
        assertEquals(verifierUserInfoDTO.getTelephone(), verificationBodyUser.getTelephone());
        assertEquals(verifierUserInfoDTO.getEmail(), verificationBodyUser.getEmail());
    }

    private VerificationBodyUser createVerificationBodyUser(String verificationBodyName, InstallationAccountStatus accountStatus,
                                                            String accreditationNumber, String role, AuthorityStatus authorityStatus,
                                                            String userId) {

        return VerificationBodyUser.builder()
                .verificationBodyName(verificationBodyName)
                .accountStatus(accountStatus.name())
                .accreditationReferenceNumber(accreditationNumber)
                .isAccreditedForUKETSInstallations(true)
                .isAccreditedForEUETSInstallations(false)
                .isAccreditedForUKETSAviation(false)
                .isAccreditedForCorsia(true)
                .role(role)
                .authorityStatus(authorityStatus.name())
                .userId(userId)
                .build();
    }

    private VerifierUserInfoDTO createOperatorUserInfoDTO(String id, String firstname, String lastname, String phoneNumber,
                                                          String email, LocalDateTime lastLoginDate) {

        return VerifierUserInfoDTO.builder()
                .id(id)
                .firstName(firstname)
                .lastName(lastname)
                .phoneNumber(phoneNumber)
                .email(email)
                .lastLoginDate(lastLoginDate.toString())
                .build();
    }

}
