package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_USER_ROLE_CODE;

@ExtendWith(MockitoExtension.class)
class UserReportEntryGeneratorHandlerTest {

    private UserReportEntryGeneratorHandler handler;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserReportEntryRepository userReportEntryRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        handler = new TestUserReportEntryGeneratorHandler(userAuthService, userReportEntryRepository);
    }

    @Test
    void generate_withBaseEntriesRegulatorsAndVerifiers() {
        UserReportEntry baseEntry = UserReportEntry.builder()
                .userAccountId("user1")
                .role("operator")
                .userAccountStatus("ACTIVE")
                .contactTypes(List.of("PRIMARY"))
                .build();
        List<UserReportEntry> baseEntries = new ArrayList<>(List.of(baseEntry));

        Authority regulator = mockAuthority("user2", "regulator", AuthorityStatus.ACTIVE);
        Authority verifier = mockAuthority("user3", VERIFIER_ADMIN_ROLE_CODE, AuthorityStatus.ACTIVE);

        when(userReportEntryRepository.findUserReportEntries(entityManager, AccountType.INSTALLATION))
                .thenReturn(baseEntries);
        when(userReportEntryRepository.findByCompetentAuthorityIsNotNull(entityManager))
                .thenReturn(List.of(regulator));
        when(userReportEntryRepository.findByCodeIn(entityManager,
                List.of(VERIFIER_ADMIN_ROLE_CODE, VERIFIER_USER_ROLE_CODE)))
                .thenReturn(List.of(verifier));

        UserReportInfoDTO dto = mock(UserReportInfoDTO.class);
        when(dto.getId()).thenReturn("user1");
        when(dto.getFullName()).thenReturn("John Doe");
        when(dto.getTelephone()).thenReturn("123456");
        when(dto.getLastLoginDate()).thenReturn("2023-05-15T10:30:45");
        when(dto.getEmail()).thenReturn("john@example.com");
        when(userAuthService.getUsersWithAttributes(anyList(), eq(UserReportInfoDTO.class)))
                .thenReturn(List.of(dto));

        List<UserReportEntry> result = handler.generate(entityManager, AccountType.INSTALLATION);

        assertThat(result).hasSize(3);

        UserReportEntry enriched = result.get(0);
        assertThat(enriched.getUserAccountId()).isEqualTo("user1");
        assertThat(enriched.getFullName()).isEqualTo("John Doe");
        assertThat(enriched.getTelephone()).isEqualTo("123456");
        assertThat(enriched.getLastLogin()).isEqualTo("15 May 2023 10:30:45");
        assertThat(enriched.getEmail()).isEqualTo("john@example.com");

        assertThat(result.get(1).getUserAccountId()).isEqualTo("user2");
        assertThat(result.get(2).getUserAccountId()).isEqualTo("user3");
    }

    @Test
    void generate_withNoAuthorities() {
        UserReportEntry baseEntry = UserReportEntry.builder().userAccountId("user1").build();
        List<UserReportEntry> baseEntries = new ArrayList<>(List.of(baseEntry));

        when(userReportEntryRepository.findUserReportEntries(entityManager, AccountType.AVIATION))
                .thenReturn(baseEntries);
        when(userReportEntryRepository.findByCompetentAuthorityIsNotNull(entityManager))
                .thenReturn(List.of());
        when(userReportEntryRepository.findByCodeIn(entityManager,
                List.of(VERIFIER_ADMIN_ROLE_CODE, VERIFIER_USER_ROLE_CODE)))
                .thenReturn(List.of());
        when(userAuthService.getUsersWithAttributes(anyList(), eq(UserReportInfoDTO.class)))
                .thenReturn(List.of());

        List<UserReportEntry> result = handler.generate(entityManager, AccountType.AVIATION);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserAccountId()).isEqualTo("user1");
        assertThat(result.get(0).getFullName()).isNull();
    }

    @Test
    void authoritiesToUserReportEntries() {
        Authority authority = mockAuthority("user1", "operator", AuthorityStatus.ACTIVE);

        List<UserReportEntry> result = handler.authoritiesToUserReportEntries(List.of(authority));

        assertThat(result).hasSize(1);
        UserReportEntry entry = result.get(0);
        assertThat(entry.getUserAccountId()).isEqualTo("user1");
        assertThat(entry.getRole()).isEqualTo("operator");
        assertThat(entry.getUserAccountStatus()).isEqualTo("ACTIVE");
        assertThat(entry.getContactTypes()).isEmpty();
    }

    @Test
    void authoritiesToUserReportEntries_withNullCodeAndStatus() {
        Authority authority = mockAuthority("user1", null, null);

        List<UserReportEntry> result = handler.authoritiesToUserReportEntries(List.of(authority));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isNull();
        assertThat(result.get(0).getUserAccountStatus()).isNull();
    }

    @Test
    void getUsersWithAttributes_emptyList_returnsEmptyMapAndSkipsService() {
        Map<String, UserReportInfoDTO> result = handler.getUsersWithAttributes(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(userAuthService);
    }

    @Test
    void appendUserDetails() {
        UserReportEntry entry = UserReportEntry.builder().userAccountId("user1").build();
        UserReportInfoDTO dto = mock(UserReportInfoDTO.class);
        when(dto.getFullName()).thenReturn("John Doe");
        when(dto.getTelephone()).thenReturn("123456");
        when(dto.getLastLoginDate()).thenReturn("2023-05-15T10:30:45");
        when(dto.getEmail()).thenReturn("john@example.com");

        handler.appendUserDetails(entry, dto);

        assertThat(entry.getFullName()).isEqualTo("John Doe");
        assertThat(entry.getTelephone()).isEqualTo("123456");
        assertThat(entry.getLastLogin()).isEqualTo("15 May 2023 10:30:45");
        assertThat(entry.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void appendUserDetails_nullDto_doesNothing() {
        UserReportEntry entry = UserReportEntry.builder().userAccountId("user1").build();

        handler.appendUserDetails(entry, null);

        assertThat(entry.getFullName()).isNull();
        assertThat(entry.getEmail()).isNull();
    }

    @Test
    void formatLastLoginDate() {
        assertThat(handler.formatLastLoginDate("2023-05-15T10:30:45"))
                .isEqualTo("15 May 2023 10:30:45");
    }

    @Test
    void formatLastLoginDate_null_returnsNull() {
        assertThat(handler.formatLastLoginDate(null)).isNull();
    }

    @Test
    void buildResult() {
        List<UserReportEntry> payload =
                List.of(UserReportEntry.builder().userAccountId("user1").build());

        UsersMiReportResult result = handler.buildResult(payload, "USER");

        assertThat(result.getReportType()).isEqualTo("USER");
        assertThat(result.getResults()).isEqualTo(payload);
        assertThat(result.getColumnNames()).isEqualTo(UserReportEntry.getColumnNames());
    }

    private Authority mockAuthority(String userId, String code, AuthorityStatus status) {
        Authority authority = mock(Authority.class);
        when(authority.getUserId()).thenReturn(userId);
        when(authority.getCode()).thenReturn(code);
        when(authority.getStatus()).thenReturn(status);
        return authority;
    }

    static class TestUserReportEntryGeneratorHandler extends UserReportEntryGeneratorHandler {
        TestUserReportEntryGeneratorHandler(UserAuthService userAuthService,
                                            UserReportEntryRepository userReportEntryRepository) {
            super(userAuthService, userReportEntryRepository);
        }
    }
}
