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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyQueryServiceTest {
    
    @InjectMocks
    private VerificationBodyQueryService service;
    
    @Mock
    private VerificationBodyRepository verificationBodyRepository;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Test
    void getVerificationBodies() {
        final AppUser appUser = AppUser.builder()
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .permissions(List.of(Permission.PERM_VB_MANAGE))
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
                        .permissions(List.of(Permission.PERM_VB_MANAGE))
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
    
    @Test
    void getVerificationBodyOptById() {
        Long verificationBodyId = 1L;
        VerificationBody vb = VerificationBody.builder().name("vb").id(verificationBodyId).build();
        
        when(verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId))
            .thenReturn(Optional.of(vb));
        
        Optional<VerificationBodyDTO> result = service.getVerificationBodyOptById(verificationBodyId);
        
        assertThat(result.get()).isEqualTo(VerificationBodyDTO.builder().name("vb").id(verificationBodyId).build());
        verify(verificationBodyRepository, times(1)).findByIdEagerEmissionTradingSchemes(verificationBodyId);
    }
    
    @Test
    void getVerificationBodyOptById_not_found() {
        Long verificationBodyId = 1L;
        
        when(verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId))
            .thenReturn(Optional.empty());
        
        Optional<VerificationBodyDTO> result = service.getVerificationBodyOptById(verificationBodyId);
        
        assertThat(result).isEmpty();
        verify(verificationBodyRepository, times(1)).findByIdEagerEmissionTradingSchemes(verificationBodyId);
    }
    
    @Test
    void getVerificationBodyById() {
        Long verificationBodyId = 1L;
        VerificationBody vb = VerificationBody.builder().name("vb").id(verificationBodyId).build();
        
        when(verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId))
            .thenReturn(Optional.of(vb));
        
        VerificationBodyDTO result = service.getVerificationBodyById(verificationBodyId);
        
        assertThat(result.getId()).isEqualTo(verificationBodyId);
        verify(verificationBodyRepository, times(1)).findByIdEagerEmissionTradingSchemes(verificationBodyId);
    }
    
    @Test
    void getVerificationBodyById_not_found() {
        Long verificationBodyId = 1L;
        
        when(verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId))
            .thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class, () ->
                service.getVerificationBodyById(verificationBodyId));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(verificationBodyRepository, times(1)).findByIdEagerEmissionTradingSchemes(verificationBodyId);
    }
    
    @Test
    void getVerificationBodyNameInfoById() {
        Long verificationBodyId = 1L;
        VerificationBody vb = VerificationBody.builder().name("vb").id(verificationBodyId).build();
        
        when(verificationBodyRepository.findById(verificationBodyId))
            .thenReturn(Optional.of(vb));
        
        VerificationBodyNameInfoDTO result = service.getVerificationBodyNameInfoById(verificationBodyId);
        
        assertThat(result.getId()).isEqualTo(verificationBodyId);
        assertThat(result.getName()).isEqualTo("vb");
        
        verify(verificationBodyRepository, times(1)).findById(verificationBodyId);
    }
    
    @Test
    void getVerificationBodyNameInfoById_db_not_found() {
        Long verificationBodyId = 1L;
        
        when(verificationBodyRepository.findById(verificationBodyId))
            .thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class, () ->
                service.getVerificationBodyNameInfoById(verificationBodyId));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(verificationBodyRepository, times(1)).findById(verificationBodyId);
    }
    
    @Test
    void getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme() {
        EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        List<VerificationBodyNameInfoDTO> verificationBodies = List
                .of(VerificationBodyNameInfoDTO.builder().id(1L).name("1").build());
        
        when(verificationBodyRepository.findActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets))
            .thenReturn(verificationBodies);
    
        List<VerificationBodyNameInfoDTO> result = service.getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets);
        assertThat(result).isEqualTo(verificationBodies);
        
        verify(verificationBodyRepository, times(1)).findActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets);
    }

    @Test
    void getAllActiveVerificationBodiesAccreditedToEmissionTradingSchemeSorted() {
        EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        VerificationBodyNameInfoDTO vb1 = VerificationBodyNameInfoDTO.builder().id(1L).name("1").build();
        VerificationBodyNameInfoDTO vb2 = VerificationBodyNameInfoDTO.builder().id(2L).name("Zzzzzz").build();
        VerificationBodyNameInfoDTO vb3 = VerificationBodyNameInfoDTO.builder().id(3L).name("abCd").build();
        VerificationBodyNameInfoDTO vb4 = VerificationBodyNameInfoDTO.builder().id(4L).name("AbdC").build();
        VerificationBodyNameInfoDTO vb5 = VerificationBodyNameInfoDTO.builder().id(5L).name("2").build();

        List<VerificationBodyNameInfoDTO> unsortedVerificationBodies = List.of(vb1, vb2, vb3, vb4, vb5);
        List<VerificationBodyNameInfoDTO> sortedVerificationBodies = List.of(vb1, vb5, vb3, vb4, vb2);

        when(verificationBodyRepository.findActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets))
            .thenReturn(unsortedVerificationBodies);

        List<VerificationBodyNameInfoDTO> result = service.getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets);
        assertThat(result).isEqualTo(sortedVerificationBodies);

        verify(verificationBodyRepository, times(1)).findActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets);
    }
    
    @Test
    void existsVerificationBodyById() {
        Long verificationBodyId = 1L;
        
        when(verificationBodyRepository.existsById(verificationBodyId))
            .thenReturn(true);
        
        boolean result = service.existsVerificationBodyById(verificationBodyId);
        
        assertThat(result).isTrue();
        verify(verificationBodyRepository, times(1)).existsById(verificationBodyId);
    }
    
    @Test
    void isVerificationBodyAccreditedToEmissionTradingScheme() {
        Long verificationBodyId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        
        when(verificationBodyRepository.isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, emissionTradingScheme))
            .thenReturn(true);
        
        boolean result = service.isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, emissionTradingScheme);
        assertThat(result).isTrue();
        verify(verificationBodyRepository, times(1)).isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, emissionTradingScheme);
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
