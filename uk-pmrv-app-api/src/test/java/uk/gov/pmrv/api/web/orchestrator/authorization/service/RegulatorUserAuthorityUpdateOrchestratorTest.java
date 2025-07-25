package uk.gov.pmrv.api.web.orchestrator.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityUpdateService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserNotificationGateway;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAuthorityUpdateOrchestratorTest {

    @InjectMocks
    private RegulatorUserAuthorityUpdateOrchestrator orchestrator;

    @Mock
    private RegulatorAuthorityUpdateService regulatorAuthorityUpdateService;
    
    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Test
    void updateRegulatorUsersStatus_WhenNoExceptions_thenFlowCompletes() {

        final RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus1 =
            RegulatorUserUpdateStatusDTO.builder().userId("user1").authorityStatus(AuthorityStatus.ACTIVE).build();
        final RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus2 =
            RegulatorUserUpdateStatusDTO.builder().userId("user2").authorityStatus(AuthorityStatus.ACTIVE).build();
        final List<RegulatorUserUpdateStatusDTO>
            regulatorUsers = List.of(regulatorUserUpdateStatus1, regulatorUserUpdateStatus2);
        final AppUser authUser = buildRegulatorUser("regUserId", ENGLAND);

        when(regulatorAuthorityUpdateService.updateRegulatorUsersStatus(regulatorUsers, authUser))
            .thenReturn(List.of("user1"));

        orchestrator.updateRegulatorUsersStatus(regulatorUsers, authUser);

        verify(regulatorAuthorityUpdateService, times(1)).updateRegulatorUsersStatus(regulatorUsers, authUser);
        verify(regulatorUserNotificationGateway, times(1)).sendUpdateNotifications(List.of("user1"));
    }

    private AppUser buildRegulatorUser(final String userId, final CompetentAuthorityEnum ca) {
        return AppUser.builder()
            .userId(userId)
            .roleType(RoleTypeConstants.REGULATOR)
            .authorities(
                List.of(AppAuthority.builder()
                    .competentAuthority(ca)
                    .build()
                )
            )
            .build();
    }
}