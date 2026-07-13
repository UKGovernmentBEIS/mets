package uk.gov.pmrv.api.mireport.system.aviation.userreportentry;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportEntry;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportEntryRepository;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UserReportInfoDTO;
import uk.gov.pmrv.api.mireport.system.common.userreportentry.UsersMiReportResult;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationUserReportEntryGeneratorHandlerTest {

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserReportEntryRepository userReportEntryRepository;

    @Mock
    private EntityManager entityManager;

    private AviationUserReportEntryGeneratorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AviationUserReportEntryGeneratorHandler(
                userAuthService,
                userReportEntryRepository
        );
    }

    @Test
    void generateMiReport() {
        UserReportEntry entry = new UserReportEntry();
        entry.setUserAccountId("user1");

        when(userReportEntryRepository.findUserReportEntries(entityManager, AccountType.AVIATION))
                .thenReturn(List.of(entry));

        UserReportInfoDTO userInfo = new UserReportInfoDTO();
        userInfo.setId("user1");
        userInfo.setFirstName("John");
        userInfo.setLastName("Doe");
        userInfo.setPhoneNumberCode("123");
        userInfo.setPhoneNumber("45678");
        userInfo.setEmail("john@test.com");
        userInfo.setLastLoginDate("2024-01-01T10:00:00");

        when(userAuthService.getUsersWithAttributes(List.of("user1"), UserReportInfoDTO.class))
                .thenReturn(List.of(userInfo));

        MiReportSystemResult result = handler.generateMiReport(entityManager, new EmptyMiReportSystemParams());

        UsersMiReportResult casted = (UsersMiReportResult) result;

        assertEquals(1, casted.getResults().size());

        UserReportEntry resultEntry = casted.getResults().getFirst();

        assertEquals("John Doe", resultEntry.getFullName());
        assertEquals("+12345678", resultEntry.getTelephone());
        assertEquals("john@test.com", resultEntry.getEmail());
        assertNotNull(resultEntry.getLastLogin());

        verify(userReportEntryRepository).findUserReportEntries(entityManager, AccountType.AVIATION);

        verify(userAuthService).getUsersWithAttributes(List.of("user1"), UserReportInfoDTO.class);
    }

    @Test
    void generateMiReport_shouldNotCallUserService_whenUserIdIsNull() {
        UserReportEntry entry = new UserReportEntry();
        entry.setUserAccountId(null);

        when(userReportEntryRepository.findUserReportEntries(entityManager, AccountType.AVIATION))
                .thenReturn(List.of(entry));

        handler.generateMiReport(entityManager, new EmptyMiReportSystemParams());

        verifyNoInteractions(userAuthService);
    }
}
