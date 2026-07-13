package uk.gov.pmrv.api.verificationbody.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyViewServiceTest {

    @InjectMocks
    private VerificationBodyViewService service;

    @Mock
    private VerificationBodyRepository verificationBodyRepository;
    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Test
    void getVerificationBodies() {
        final AppUser appUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .permissions(List.of(Permission.PERM_CA_USERS_EDIT))
                .build()))
            .roleType(RoleTypeConstants.REGULATOR).build();

        List<VerificationBody> verificationBodies = List.of(buildVerificationBody(1L, "name1", VerificationBodyStatus.ACTIVE),
            buildVerificationBody(2L, "name2", VerificationBodyStatus.PENDING));
        VerificationBodyInfoResponseDTO expected = VerificationBodyInfoResponseDTO.builder()
            .verificationBodies(List.of(buildVerificationBodyInfoDTO(1L, "name1", VerificationBodyStatus.ACTIVE),
                buildVerificationBodyInfoDTO(2L, "name2", VerificationBodyStatus.PENDING)))
            .editable(true)
            .build();

        // Mock
        when(verificationBodyRepository.findAll()).thenReturn(verificationBodies);
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB))
            .thenReturn(true);

        // Invoke
        VerificationBodyInfoResponseDTO actual = service.getVerificationBodies(appUser);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyRepository, times(1)).findAll();
        verify(compAuthAuthorizationResourceService, times(1))
            .hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB);
    }

    @Test
    void getVerificationBodies_no_manage_permission() {
        final AppUser appUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .permissions(List.of(Permission.PERM_CA_USERS_EDIT))
                .build()))
            .roleType(RoleTypeConstants.REGULATOR).build();

        List<VerificationBody> verificationBodies = List.of(buildVerificationBody(1L, "name1", VerificationBodyStatus.ACTIVE),
            buildVerificationBody(2L, "name2", VerificationBodyStatus.PENDING));
        VerificationBodyInfoResponseDTO expected = VerificationBodyInfoResponseDTO.builder()
            .verificationBodies(List.of(buildVerificationBodyInfoDTO(1L, "name1", VerificationBodyStatus.ACTIVE),
                buildVerificationBodyInfoDTO(2L, "name2", VerificationBodyStatus.PENDING)))
            .editable(false)
            .build();

        // Mock
        when(verificationBodyRepository.findAll()).thenReturn(verificationBodies);
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB))
            .thenReturn(false);

        // Invoke
        VerificationBodyInfoResponseDTO actual = service.getVerificationBodies(appUser);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyRepository, times(1)).findAll();
        verify(compAuthAuthorizationResourceService, times(1))
            .hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB);
    }

    @Test
    void getVerificationBodies_empty() {
        final AppUser appUser = AppUser.builder()
            .authorities(List.of(AppAuthority.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .permissions(List.of(Permission.PERM_CA_USERS_EDIT))
                .build()))
            .roleType(RoleTypeConstants.REGULATOR).build();

        VerificationBodyInfoResponseDTO expected = VerificationBodyInfoResponseDTO.builder()
            .verificationBodies(List.of())
            .editable(true)
            .build();

        // Mock
        when(verificationBodyRepository.findAll()).thenReturn(List.of());
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB))
            .thenReturn(true);

        // Invoke
        VerificationBodyInfoResponseDTO actual = service.getVerificationBodies(appUser);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyRepository, times(1)).findAll();
        verify(compAuthAuthorizationResourceService, times(1))
            .hasUserScopeToCompAuth(appUser, Scope.MANAGE_VB);
    }

    private VerificationBody buildVerificationBody(Long id, String name, VerificationBodyStatus status) {
        return VerificationBody.builder()
            .id(id)
            .name(name)
            .status(status)
            .build();
    }

    private VerificationBodyInfoDTO buildVerificationBodyInfoDTO(Long id, String name, VerificationBodyStatus status) {
        return VerificationBodyInfoDTO.builder()
            .id(id)
            .name(name)
            .status(status)
            .build();
    }
}