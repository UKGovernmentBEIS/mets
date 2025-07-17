package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityPermission;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACCEPTED;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.DISABLED;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.TEMP_DISABLED;

@ExtendWith(MockitoExtension.class)
class RegulatorLoginStatusServiceTest {

    @InjectMocks
    private RegulatorLoginStatusService regulatorLoginStatusService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void getUserDomainsLoginStatusInfo_when_active_authorities_with_permissions_then_enabled() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(ACTIVE)
            .build();
        authority.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.ENABLED,
                AccountType.AVIATION, LoginStatus.ENABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_active_authorities_without_permissions_then_no_authority() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(ACTIVE)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY,
                AccountType.AVIATION, LoginStatus.NO_AUTHORITY
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_no_active_authorities_but_temp_disabled_then_temp_disabled() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TEMP_DISABLED)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.TEMP_DISABLED,
                AccountType.AVIATION, LoginStatus.TEMP_DISABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_nor_active_authorities_neither_temp_disabled_then_disabled() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(DISABLED)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.DISABLED,
                AccountType.AVIATION, LoginStatus.DISABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_nor_active_authorities_and_accepted() {
        final String userId = "userId";

        Authority authority = Authority.builder()
            .userId(userId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(ACCEPTED)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.ACCEPTED,
                AccountType.AVIATION, LoginStatus.ACCEPTED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_no_authorities_and_auth_status_not_deleted_then_no_authority() {
        final String userId = "userId";

        when(authorityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = regulatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY,
                AccountType.AVIATION, LoginStatus.NO_AUTHORITY
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleTypeConstants.REGULATOR, regulatorLoginStatusService.getRoleType());
    }
}