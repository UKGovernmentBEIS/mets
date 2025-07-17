package uk.gov.pmrv.api.web.orchestrator.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.RegulatorUsersAuthoritiesInfoDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAuthorityQueryOrchestratorTest {

    @InjectMocks
    private RegulatorUserAuthorityQueryOrchestrator regulatorUserAuthorityQueryOrchestrator;

    @Mock
    private RegulatorAuthorityQueryService regulatorAuthorityQueryService;

    @Mock
    private RegulatorUserInfoService regulatorUserInfoService;

    @Test
    void getCaRegulators() {
        String userId = "userId";
        AuthorityStatus status = ACTIVE;
        AppUser authUser = AppUser.builder()
                .userId("authUserId")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();
        UserAuthorityDTO userAuthority = UserAuthorityDTO.builder()
                .userId(userId)
                .authorityStatus(status)
                .build();
        UserAuthoritiesDTO userAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of(userAuthority))
                .editable(true)
                .build();
        RegulatorUserInfoDTO userInfo = RegulatorUserInfoDTO.builder().id(userId).enabled(true).build();

        RegulatorUserAuthorityInfoDTO expectedUserAuthInfo =
                RegulatorUserAuthorityInfoDTO.builder().userId(userId).authorityStatus(status).build();

        when(regulatorAuthorityQueryService.getCaAuthorities(authUser)).thenReturn(userAuthorities);
        when(regulatorUserInfoService.getRegulatorUsersInfo(authUser, List.of(userId))).thenReturn(List.of(userInfo));

        RegulatorUsersAuthoritiesInfoDTO caRegulators = regulatorUserAuthorityQueryOrchestrator.getCaUsersAuthoritiesInfo(authUser);

        assertTrue(caRegulators.isEditable());
        assertThat(caRegulators.getCaUsers()).hasSize(1);
        assertEquals(expectedUserAuthInfo, caRegulators.getCaUsers().get(0));

        verify(regulatorAuthorityQueryService, times(1)).getCaAuthorities(authUser);
        verify(regulatorUserInfoService, times(1)).getRegulatorUsersInfo(authUser, List.of(userId));
    }
}