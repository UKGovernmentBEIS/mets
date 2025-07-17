package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserMapper;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserRegistrationMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorUserAuthService {

	private final AuthService authService;
	private final UserAuthService userAuthService;
    private final OperatorUserMapper operatorUserMapper;
    private final OperatorUserRegistrationMapper operatorUserRegistrationMapper;
    
    public Optional<String> getUserIdByEmail(String email) {
    	return userAuthService.getUserByEmail(email).map(userInfo -> userInfo.getUserId());
    }

    public OperatorUserDTO getOperatorUserById(String userId) {
        return operatorUserMapper.toOperatorUserDTO(authService.getUserRepresentationById(userId));
    }

	public OperatorUserDTO registerAndEnableOperatorUser(
			OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, String email) {
		UserRepresentation userRepresentation = operatorUserRegistrationMapper
				.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, email);

		authService.registerAndEnableUserAndSetPassword(userRepresentation, operatorUserRegistrationWithCredentialsDTO.getPassword());
		
    	return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    public String registerOperatorUser(OperatorUserInvitationDTO operatorUserInvitation) {
        return updateUser(operatorUserInvitation);
    }
    
    public String updateUser(OperatorUserInvitationDTO operatorUserInvitation) {
        UserRepresentation userRepresentation = operatorUserMapper.toUserRepresentation(operatorUserInvitation);
        return authService.saveUser(userRepresentation);
    }

    public void updateUser(OperatorUserDTO updatedOperatorUserDTO) {
        UserRepresentation updatedUser = operatorUserMapper.toUserRepresentation(updatedOperatorUserDTO);
        authService.saveUser(updatedUser);
    }

	public OperatorUserDTO enableAndUpdateUserAndSetPassword(
			OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, String userId) {
        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);
        
		UserRepresentation userRepresentation = operatorUserRegistrationMapper
				.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, keycloakUser.getEmail());
        
		authService.enableAndSaveUserAndSetPassword(userRepresentation, operatorUserRegistrationWithCredentialsDTO.getPassword());

        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

	public OperatorUserDTO enableAndUpdateUser(OperatorUserRegistrationDTO operatorUserRegistrationDTO, String userId) {
        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);

		UserRepresentation userRepresentation = operatorUserRegistrationMapper
				.toUserRepresentation(operatorUserRegistrationDTO, keycloakUser.getEmail());

        authService.enableAndSaveUser(userRepresentation);
        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

    public OperatorUserDTO setUserPassword(String userId, String password) {
        UserRepresentation userRepresentation = authService.setUserPassword(userId, password);
        return operatorUserMapper.toOperatorUserDTO(userRepresentation);
    }

}
