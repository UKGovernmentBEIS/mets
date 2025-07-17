package uk.gov.pmrv.api.user.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.core.service.UserService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorCurrentUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAuthService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceDelegatorTest {

    @InjectMocks
    private UserServiceDelegator cut;

    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    
    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private VerifierUserAuthService verifierUserAuthService;
    
    @Test
    void getUserById_Operator() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.OPERATOR).build();

        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));
        
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email@email").build();
        when(operatorUserAuthService.getOperatorUserById(userId)).thenReturn(userDTO);
        
        // Invoke
        UserDTO result = cut.getUserById(userId);
        
        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(operatorUserAuthService, times(1)).getOperatorUserById(userId);
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, never()).getVerifierUserById(Mockito.anyString());
        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_Regulator() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();

        RegulatorUserDTO userDTO = RegulatorUserDTO.builder().email("email@email").build();
        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));
        when(regulatorUserAuthService.getRegulatorUserById(userId)).thenReturn(userDTO);
        
        // Invoke
        UserDTO result = cut.getUserById(userId);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(operatorUserAuthService, never()).getOperatorUserById(Mockito.anyString());
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(userId);
        verify(verifierUserAuthService, never()).getVerifierUserById(Mockito.anyString());
        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_Verifier() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.VERIFIER).build();

        VerifierUserDTO userDTO = VerifierUserDTO.builder().email("email@email").build();
        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));
        when(verifierUserAuthService.getVerifierUserById(userId)).thenReturn(userDTO);
        
        // Invoke
        UserDTO result = cut.getUserById(userId);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(operatorUserAuthService, never()).getOperatorUserById(Mockito.anyString());
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
        verifyNoInteractions(userService);
    }
    
    @Test
    void getUserById_no_role_type() {
        final String userId = "userId";

        UserDTO user = UserDTO.builder()
        		.firstName("fn").lastName("ln")
        		.build();
        
        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.empty());
        when(userService.getUserByUserId(userId)).thenReturn(user);

        // Invoke
        UserDTO result = cut.getUserById(userId);

        assertThat(result).isEqualTo(user);

        // Assert
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(userService, times(1)).getUserByUserId(userId);
        verifyNoInteractions(operatorUserAuthService, regulatorUserAuthService, verifierUserAuthService);
    }

    @Test
    void getCurrentUserDTO_Operator() {
        final String userId = "userId";
        AppUser currentUser = AppUser.builder()
        		.userId(userId)
        		.build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.OPERATOR).build();

        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));

        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email@email").build();
        when(operatorUserAuthService.getOperatorUserById(userId)).thenReturn(userDTO);

        // Invoke
        UserDTO result = cut.getCurrentUserDTO(currentUser);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(operatorUserAuthService, times(1)).getOperatorUserById(userId);
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, never()).getVerifierUserById(Mockito.anyString());
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUserDTO_Regulator() {
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String userId = "userId";
        AppUser currentUser = AppUser.builder()
        		.userId(userId)
        		.authorities(List.of(
        				AppAuthority.builder().competentAuthority(ca).build()
        				))
        		.build();

        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();

        RegulatorCurrentUserDTO userDTO = RegulatorCurrentUserDTO.builder().email("email@email").build();
        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));
        when(regulatorUserAuthService.getRegulatorCurrentUser(currentUser)).thenReturn(userDTO);

        // Invoke
        UserDTO result = cut.getCurrentUserDTO(currentUser);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(regulatorUserAuthService, times(1)).getRegulatorCurrentUser(currentUser);
        verifyNoInteractions(operatorUserAuthService, verifierUserAuthService);
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUserDTO_Verifier() {
        final String userId = "userId";
        AppUser currentUser = AppUser.builder()
        		.userId(userId)
        		.build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleTypeConstants.VERIFIER).build();

        VerifierUserDTO userDTO = VerifierUserDTO.builder().email("email@email").build();
        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.of(userRoleTypeDTO));
        when(verifierUserAuthService.getVerifierUserById(userId)).thenReturn(userDTO);

        // Invoke
        UserDTO result = cut.getCurrentUserDTO(currentUser);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(operatorUserAuthService, never()).getOperatorUserById(Mockito.anyString());
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUserDTO_no_role_type() {
        final String userId = "userId";
        AppUser currentUser = AppUser.builder()
        		.userId(userId)
        		.build();

        UserDTO user = UserDTO.builder()
        		.firstName("fn").lastName("ln")
        		.build();

        when(userRoleTypeService.getUserRoleTypeByUserIdOpt(userId)).thenReturn(Optional.empty());
        when(userService.getUserByUserId(userId)).thenReturn(user);

        // Invoke
        UserDTO result = cut.getCurrentUserDTO(currentUser);

        assertThat(result).isEqualTo(user);
        
        // Assert
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserIdOpt(userId);
        verify(userService, times(1)).getUserByUserId(userId);
        verifyNoInteractions(operatorUserAuthService, regulatorUserAuthService, verifierUserAuthService);
    }
}